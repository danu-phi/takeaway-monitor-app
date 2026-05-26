package com.phsmk.id.takeaway_monitor.data

import com.phsmk.id.takeaway_monitor.data.remote.ApiService
import com.phsmk.id.takeaway_monitor.data.remote.model.CustomerQueueData
import com.phsmk.id.takeaway_monitor.data.remote.model.OrderDetailResponse
import com.phsmk.id.takeaway_monitor.data.remote.model.ResponseData
import io.reactivex.Single
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataManager @Inject constructor(
    private val apiService: ApiService
) {
    init {
        instance = this
    }

    companion object {
        private lateinit var instance: DataManager

        fun getInstance(): DataManager = instance
    }

    fun getCustomerQueueDetail(id: String): Single<Response<ResponseData<CustomerQueueData>>> {
        return apiService.getCustomerQueueDetailRx(id)
    }

    fun getOrderDetail(orderId: String, hasDetail: String): Single<Response<OrderDetailResponse>> {
        return apiService.getOrderDetailRx(orderId, hasDetail)
    }
}
