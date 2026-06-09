package com.phsmk.id.takeaway_monitor.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phsmk.id.takeaway_monitor.data.local.PreferenceManager
import com.phsmk.id.takeaway_monitor.data.model.Order
import com.phsmk.id.takeaway_monitor.data.model.OrderStatus
import com.phsmk.id.takeaway_monitor.data.remote.ApiService
import com.phsmk.id.takeaway_monitor.data.remote.model.Ad
import com.phsmk.id.takeaway_monitor.data.remote.model.OrderedData
import com.phsmk.id.takeaway_monitor.data.remote.model.CustomerQueueData
import com.phsmk.id.takeaway_monitor.data.remote.model.OrderStatusType
import com.phsmk.id.takeaway_monitor.data.remote.model.SystemTimeData
import com.phsmk.id.takeaway_monitor.data.repository.OrderRepository
import com.phsmk.id.takeaway_monitor.util.Constants
import com.phsmk.id.takeaway_monitor.util.Utils
import com.phsmk.id.takeaway_monitor.util.Utils.isYouTubeLink
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class OrdersUiState(
    val orders: List<Order> = emptyList(),
    val apiOrders: List<OrderedData> = emptyList(),
    val customerQueueItems: List<CustomerQueueItem> = emptyList(),
    val ads: List<Ad> = emptyList(),
    val filteredOrders: List<Order> = emptyList(),
    val selectedStatus: OrderStatus? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val systemTime: SystemTimeData? = null,
    val serverTime: Date? = null,
    val timeFetchError: String? = null,
    val outletLogoPath: String? = null
)

sealed class CustomerQueueItem {
    data class Header(val title: String, val paxList: List<Int>) : CustomerQueueItem()
    data class Order(val data: CustomerQueueData) : CustomerQueueItem()
}

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val apiService: ApiService,
    private val preferenceManager: PreferenceManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrdersUiState())
    val uiState: StateFlow<OrdersUiState> = _uiState.asStateFlow()

    private var clockJob: Job? = null
    private var allCustomerQueues = mutableListOf<CustomerQueueData>()
    private val orderRemovalJobs = mutableMapOf<String, Job>()

    init {
        val posConfig = preferenceManager.getPosConfig()
        val logoUrl = posConfig?.outletLogo
        val logoPath = if (!logoUrl.isNullOrEmpty()) {
            val fileName = Utils.getFileNameFromUrl(logoUrl)
            val file = File(context.filesDir, "${Constants.LOGO_FOLDER}/$fileName")
            if (file.exists()) file.absolutePath else null
        } else null

        _uiState.update { it.copy(outletLogoPath = logoPath) }

        observeOrders()
        fetchSystemTime()
        fetchMedia()
    }

    private fun observeOrders() {
        viewModelScope.launch {
            orderRepository.getAllOrders().collect { orders ->
                _uiState.update { state ->
                    val filtered = applyFilters(orders, state.selectedStatus, state.searchQuery)
                    state.copy(orders = orders, filteredOrders = filtered, isLoading = false)
                }
            }
        }
    }

    fun filterByStatus(status: OrderStatus?) {
        _uiState.update { state ->
            state.copy(
                selectedStatus = status,
                filteredOrders = applyFilters(state.orders, status, state.searchQuery)
            )
        }
    }

    fun searchOrders(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                filteredOrders = applyFilters(state.orders, state.selectedStatus, query)
            )
        }
    }

    fun updateOrderStatus(orderId: String, status: OrderStatus) {
        viewModelScope.launch {
            try {
                orderRepository.updateOrderStatus(orderId, status)
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message) }
            }
        }
    }

    fun fetchSystemTime() {
        viewModelScope.launch {
            try {
                val response = apiService.getTime()
                val data = response.data
                Log.d("OrdersViewModel", "system time: ${data?.datetime}")

                if (data != null) {
                    _uiState.update { it.copy(systemTime = data, timeFetchError = null) }

                    // Parse datetime to Date
                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    val date = try {
                        data.datetime?.let { sdf.parse(it) } ?: Date()
                    } catch (e: Exception) {
                        Date()
                    }
                    startClock(date)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(timeFetchError = "Cannot get server time. Please check again!") }
            }
        }
    }

    fun fetchMedia() {
        viewModelScope.launch {
            try {
                val response = apiService.getAdsMonitor()
                response.data?.let { mediaData ->
//                    _uiState.update { it.copy(ads = mediaData.ads) }


                    if (PreferenceManager.instance.getPosConfig()!!.isShowTakeawayAds()) {
                        // Start downloading medias
                        downloadMedias(mediaData.ads)
                    }
                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    val timestamp = try {
                        mediaData.timestamp?.let { sdf.parse(it) }
                    } catch (e: Exception) {
                        null
                    }
                    
                    timestamp?.let { ts ->
                        fetchOrders(ts)
                        startClock(ts)
                    }
                }
            } catch (e: Exception) {
                Log.e("OrdersViewModel", "Error fetching media: ${e.message}")
            }
        }
    }

    fun fetchOrders(timestamp: Date = Date()) {
        viewModelScope.launch {
            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val timestampStr = sdf.format(timestamp)
                val response = apiService.getOrders()
                
                val orders = response.orderData?.orderDetail
                val filteredOrders = orders?.filter {
                    val updatedAt = try {
                        it.updatedDate?.let { dateStr -> sdf.parse(dateStr) }
                    } catch (e: Exception) {
                        null
                    }

                    val isCorrectType = it.orderTypeId == Constants.ORDER_TYPE.TAKEAWAY

                    if (updatedAt != null) {
                        val diffInSec = (timestamp.time - updatedAt.time) / 1000
                        // Remove Ready order after 5 minutes (300 seconds)
                        val isReadyStatus = it.orderStatusId == OrderStatusType.PICKEDUP.statusNumber ||
                                it.orderStatusId == OrderStatusType.FINISHED.statusNumber ||
                                it.orderStatusId == OrderStatusType.CHECKOUT.statusNumber

                        isCorrectType && !(diffInSec > 300 && isReadyStatus)
                    } else {
                        isCorrectType
                    }
                }

                filteredOrders?.forEachIndexed { index, orderedData ->
                    orderedData.orderNo = index + 1
                }

                _uiState.update { 
                    it.copy(
                        apiOrders = filteredOrders ?: emptyList()
                    )
                }
            } catch (e: Exception) {
                Log.e("OrdersViewModel", "Error fetching orders: ${e.message}")
            }
        }
    }

    private fun downloadMedias(medias: List<Ad>) {
        viewModelScope.launch {
            val updatedMedias = medias.map { ad ->
                var path = ad.url
                if (path != null && path.contains("ph_posmanager.phsmk.id")) {
                    path = path.replace("ph_posmanager.phsmk.id", "ph-posmanager.phsmk.id")
                }

                if (path == null) {
                    ad
                } else if (path.isYouTubeLink()) {
                    ad // YouTube links don't need downloading for now in this context
                } else {
                    val filename = path.substringAfterLast("/").replace("%20", "_")
                    val directory = File(context.filesDir, "assets")
                    if (!directory.exists()) directory.mkdirs()
                    
                    val destinationFile = File(directory, filename)
                    
                    if (destinationFile.exists()) {
                        ad.copy(localPath = destinationFile.absolutePath)
                    } else {
                        try {

                            val response = apiService.downloadFile(path)
                            if (response.isSuccessful) {
                                val body = response.body()
                                if (body != null) {
                                    saveToDisk(body, destinationFile)
                                    ad.copy(localPath = destinationFile.absolutePath)
                                } else ad
                            } else ad
                        } catch (e: Exception) {
                            Log.e("OrdersViewModel", "Error downloading ${path}: ${e.message}")
                            ad
                        }
                    }
                }
            }
            _uiState.update { it.copy(ads = updatedMedias) }
        }
    }

    private suspend fun saveToDisk(body: okhttp3.ResponseBody, file: File) {
        withContext(Dispatchers.IO) {
            val inputStream = body.byteStream()
            val outputStream = FileOutputStream(file)
            val buffer = ByteArray(4096)
            var bytesRead: Int
            try {
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }
                outputStream.flush()
            } finally {
                outputStream.close()
                inputStream.close()
            }
        }
    }

    private fun startClock(baseDate: Date) {
        clockJob?.cancel()
        val calendar = Calendar.getInstance().apply { time = baseDate }
        clockJob = viewModelScope.launch {
            var counter = 0
            while (true) {
                _uiState.update { it.copy(serverTime = calendar.time) }
                delay(1000)
                calendar.add(Calendar.SECOND, 1)
                counter++
                
                // Fetch customer queue every 10 seconds (adjust as needed)
                if (counter % 10 == 0) {
                    fetchCustomerQueue()
//                    fetchOrders(calendar.time)
                }
            }
        }
    }

    private fun fetchCustomerQueue() {
        viewModelScope.launch {
            try {
                val response = apiService.getCustomerQueue()
                response.data?.let { queueDataList ->
                    allCustomerQueues = queueDataList.toMutableList()
                    _uiState.update { 
                        it.copy(customerQueueItems = filterCustomerQueueForDisplay(allCustomerQueues))
                    }
                }
            } catch (e: Exception) {
                Log.e("OrdersViewModel", "Error fetching customer queue: ${e.message}")
            }
        }
    }

    fun onOrderReceived(action: String, orderDetail: OrderedData) {
        Log.d("OrdersViewModel", "onOrderReceived: customerName=${orderDetail.name}, orderStatusId=${orderDetail.orderStatusId}, orderStatusType=${OrderStatusType.statusOf(orderDetail.orderStatusId)}")
        
        val orderId = orderDetail.id ?: return

        // Cancel existing timer for this order if any
        orderRemovalJobs[orderId]?.cancel()

        _uiState.update { state ->
            val currentOrders = state.apiOrders.toMutableList()
            val index = currentOrders.indexOfFirst { it.id == orderId }
            if (index != -1) {
                currentOrders[index] = orderDetail
            } else {
                currentOrders.add(0, orderDetail)
            }
            // Re-apply order numbers
            currentOrders.forEachIndexed { i, data -> data.orderNo = i + 1 }
            state.copy(apiOrders = currentOrders)
        }

        // Start 5-minute removal timer if status is Finished/Ready
        val isReadyStatus = orderDetail.orderStatusId == OrderStatusType.FINISHED.statusNumber ||
                orderDetail.orderStatusId == OrderStatusType.PICKEDUP.statusNumber ||
                orderDetail.orderStatusId == OrderStatusType.CHECKOUT.statusNumber

        if (isReadyStatus) {
            orderRemovalJobs[orderId] = viewModelScope.launch {
                delay(5 * 60 * 1000) // 5 minutes
                removeOrder(orderId)
                orderRemovalJobs.remove(orderId)
            }
        }
    }

    private fun removeOrder(orderId: String) {
        _uiState.update { state ->
            val currentOrders = state.apiOrders.filter { it.id != orderId }
            currentOrders.forEachIndexed { i, data -> data.orderNo = i + 1 }
            state.copy(apiOrders = currentOrders)
        }
    }

    fun onCustomerQueueReceived(action: String, queueData: CustomerQueueData) {
        val index = allCustomerQueues.indexOfFirst { it.id == queueData.id }
        if (index != -1) {
            allCustomerQueues[index] = queueData
        } else {
            allCustomerQueues.add(0, queueData)
        }
        _uiState.update { 
            it.copy(customerQueueItems = filterCustomerQueueForDisplay(allCustomerQueues))
        }
    }

    fun onCustomerQueueDeleted(id: Int) {
        val removed = allCustomerQueues.removeAll { it.id == id.toString() }
        if (removed) {
            _uiState.update { 
                it.copy(customerQueueItems = filterCustomerQueueForDisplay(allCustomerQueues))
            }
        }
    }

    fun dismissTimeError() {
        _uiState.update { it.copy(timeFetchError = null) }
    }

    private fun filterCustomerQueueForDisplay(allQueue: List<CustomerQueueData>): List<CustomerQueueItem> {
        val result = mutableListOf<CustomerQueueItem>()
        val listItem = ArrayList(allQueue)
        val config = preferenceManager.getPosConfig() ?: return allQueue.map { CustomerQueueItem.Order(it) }
        val listGroupByPax = config.listGroupByPax

        if (listGroupByPax != null) {
            val listRemoveItem = ArrayList<CustomerQueueData>()
            for (paxGroup in listGroupByPax) {
                var limit = 2
                result.add(CustomerQueueItem.Header(paxGroup.joinToString(separator = "-"), paxGroup))
                for (item in listItem) {
                    for (pax in paxGroup) {
                        if (pax == item.capacity?.toIntOrNull()) {
                            if (limit > 0) {
                                result.add(CustomerQueueItem.Order(item))
                                limit -= 1
                            }
                            listRemoveItem.add(item)
                        }
                    }
                }
            }
            for (item in listRemoveItem) {
                listItem.remove(item)
            }
            
            result.add(CustomerQueueItem.Header("Group", emptyList()))
            var cusLimit = 2
            for (item in listItem) {
                if (cusLimit > 0) {
                    result.add(CustomerQueueItem.Order(item))
                    cusLimit -= 1
                }
            }
        } else {
            // Default behavior if no grouping config
            return allQueue.map { CustomerQueueItem.Order(it) }
        }

        if (result.isEmpty()) return emptyList()

        if (config.isDisplayEmptyGroup.isNullOrEmpty() || config.isDisplayEmptyGroup != "1") {
            // Remove empty groups
            val filteredResult = mutableListOf<CustomerQueueItem>()
            for (i in 0 until result.size) {
                val current = result[i]
                if (current is CustomerQueueItem.Header) {
                    val hasData = if (i + 1 < result.size) result[i + 1] is CustomerQueueItem.Order else false
                    if (hasData) {
                        filteredResult.add(current)
                    }
                } else {
                    filteredResult.add(current)
                }
            }
            return filteredResult
        }

        return result
    }

    override fun onCleared() {
        super.onCleared()
        clockJob?.cancel()
        orderRemovalJobs.values.forEach { it.cancel() }
        orderRemovalJobs.clear()
    }

    private fun applyFilters(
        orders: List<Order>,
        status: OrderStatus?,
        query: String
    ): List<Order> = orders
        .filter { status == null || it.status == status }
        .filter { query.isBlank() || it.customerName.contains(query, ignoreCase = true) }
}
