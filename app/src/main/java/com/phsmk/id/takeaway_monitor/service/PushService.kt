package com.phsmk.id.takeaway_monitor.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.phsmk.id.takeaway_monitor.data.DataManager
import com.phsmk.id.takeaway_monitor.data.local.PreferenceManager
import com.phsmk.id.takeaway_monitor.data.remote.model.CustomerQueueData
import com.phsmk.id.takeaway_monitor.data.remote.model.OrderedData
import com.phsmk.id.takeaway_monitor.data.remote.model.ResponseData
import com.phsmk.id.takeaway_monitor.util.Utils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject
import retrofit2.Response
import timber.log.Timber

class PushService : Service() {

    private var mSocket: Socket? = null
    private var mGetOrderDisposable: Disposable? = null

    private lateinit var mDataManager: DataManager
    private lateinit var mDisposables: CompositeDisposable
    private lateinit var mLocalBroadcastManager: LocalBroadcastManager

    companion object {

        const val ACTION_SEND_CREATED = "action_send_created_order"
        const val ACTION_SEND_UPDATED = "action_send_updated_order"

        const val ACTION_CUSTOMER_QUEUE_CREATED = "action_create_customer_queue"
        const val ACTION_CUSTOMER_QUEUE_UPDATED = "action_update_customer_queue"
        const val ACTION_CUSTOMER_QUEUE_DELETED = "action_delete_customer_queue"

        const val EVENT_SESSION_LOGOUT = "login"
        const val EXT_ORDER_DETAIL = "extra_order_id"
        const val EXT_CUSTOMER_QUEUE_DETAIL = "extra_customer_queue"

        const val SOCKET_RECEIVED_EVENT_CREATE_ORDER = "order-created"
        const val SOCKET_RECEIVED_EVENT_EDIT_ORDER = "order-updated"
        const val SOCKET_RECEIVED_EVENT_CREATE_ADVANCE_ORDER = "advance-order-created"

        const val SOCKET_RECEIVED_EVENT_ADD_QUEUE = "add-queue"
        const val SOCKET_RECEIVED_EVENT_UPDATE_QUEUE = "update-queue"
        const val SOCKET_RECEIVED_EVENT_DELETE_QUEUE = "delete-queue"

        fun getIntent(context: Context): Intent = Intent(context, PushService::class.java)
    }

    override fun onCreate() {
        super.onCreate()
        mDataManager = DataManager.getInstance()
        mDisposables = CompositeDisposable()
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    @Suppress("DEPRECATION")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (intent != null) Timber.w(intent.toUri(0))

        val okHttpClient = Utils.getUnsafeOkHttpClient()
        IO.setDefaultOkHttpCallFactory(okHttpClient)
        IO.setDefaultOkHttpWebSocketFactory(okHttpClient)

        val webSocketHost = PreferenceManager.instance.getPosConfig()?.urlSocket
            ?.replace("ph_pos.phsmk.id", "ph-pos.phsmk.id")
        Timber.w("socket url: $webSocketHost")

        if (webSocketHost.isNullOrEmpty()) return START_NOT_STICKY

        val finalUrl = webSocketHost.trim()
        val token = PreferenceManager.instance.userToken
        Timber.w("Connecting to Socket: $finalUrl with token: $token")

        val options = IO.Options().apply {
            query = "token=$token"
            callFactory = okHttpClient
            webSocketFactory = okHttpClient
            forceNew = true
            reconnection = true
            timeout = 60000
            transports = arrayOf("websocket")
        }

        mSocket = IO.socket(finalUrl, options)
        mSocket?.let { socket ->

            socket.on(Socket.EVENT_CONNECT) {
                Timber.w("Socket connected!")
                socket.emit(EVENT_SESSION_LOGOUT, pushToken())
            }.on(SOCKET_RECEIVED_EVENT_CREATE_ORDER) {

                Timber.w("order-created")

                if (it != null && (it[0] as JSONObject).optString("order_id") != null) {
                    val orderId = (it[0] as JSONObject).getString("order_id")
                    getOrderById(orderId, ACTION_SEND_CREATED)
                }
            }.on(SOCKET_RECEIVED_EVENT_CREATE_ADVANCE_ORDER) {

                Timber.w("advance-order-created")

                if (it != null && (it[0] as JSONObject).optString("order_id") != null) {
                    val orderId = (it[0] as JSONObject).getString("order_id")
                    getOrderById(orderId, ACTION_SEND_CREATED)
                }
            }.on(SOCKET_RECEIVED_EVENT_EDIT_ORDER) {

                if (it != null && (it[0] as JSONObject).optString("order_id") != null) {
                    val orderId = (it[0] as JSONObject).getString("order_id")

                    // If orderId returned is current editing order id, do nothing
                    val editingOrderId = PreferenceManager.instance.editingOrderId

                    Timber.w("order-updated: $orderId")

                    if (editingOrderId != null && editingOrderId == orderId) return@on

                    getOrderById(orderId, ACTION_SEND_UPDATED)
                }
            }.on(SOCKET_RECEIVED_EVENT_ADD_QUEUE) {

                onReceiveSocket(SOCKET_RECEIVED_EVENT_ADD_QUEUE, it)
            }.on(SOCKET_RECEIVED_EVENT_UPDATE_QUEUE) {

                onReceiveSocket(SOCKET_RECEIVED_EVENT_UPDATE_QUEUE, it)
            }.on(SOCKET_RECEIVED_EVENT_DELETE_QUEUE) {

                onReceiveSocket(SOCKET_RECEIVED_EVENT_DELETE_QUEUE, it)
            }.on(Socket.EVENT_CONNECT_ERROR) {
                if (it != null && it.isNotEmpty()) {
                    val error = it[0]
                    if (error is Exception) {
                        Timber.e(error, "Socket Connect Error: ${error.message}")
                        error.cause?.let { cause -> Timber.e(cause, "Socket Connect Error Cause: ${cause.message}") }
                        if (error is io.socket.engineio.client.EngineIOException) {
                            Timber.e("EngineIOException code: ${error.code}")
                        }
                    } else {
                        Timber.w("Socket Connect Error: $error")
                    }
                } else {
                    Timber.w("EVENT_CONNECT_ERROR")
                }
            }.on(Socket.EVENT_DISCONNECT) {
                Timber.w("Socket disconnected!")
            }

            return@let socket.connect()
        }

        return START_NOT_STICKY
    }

    private fun onReceiveSocket(action: String, it: Array<out Any>?) {
        try {
            when(action){
                SOCKET_RECEIVED_EVENT_ADD_QUEUE -> {
                    if (it != null && (it[0] as JSONObject).optString("customer_queue_id") != null) {
                        val customerQueueId = (it[0] as JSONObject).getString("customer_queue_id")
                        getCustomerQueueDetail(customerQueueId, ACTION_CUSTOMER_QUEUE_CREATED)
                        Timber.w("create - customer-queue-id: $customerQueueId")
                    }
                }
                SOCKET_RECEIVED_EVENT_UPDATE_QUEUE -> {
                    if (it != null && (it[0] as JSONObject).optString("customer_queue_id") != null) {
                        val customerQueueId = (it[0] as JSONObject).getString("customer_queue_id")
                        getCustomerQueueDetail(customerQueueId, ACTION_CUSTOMER_QUEUE_UPDATED)
                        Timber.w("update - customer-queue-id: $customerQueueId")
                    }
                }
                SOCKET_RECEIVED_EVENT_DELETE_QUEUE -> {
                    if (it != null && (it[0] as JSONObject).optString("customer_queue_id") != null) {
                        val customerQueueId = (it[0] as JSONObject).getString("customer_queue_id")
                        val intt = Intent(ACTION_CUSTOMER_QUEUE_DELETED)
                        intt.putExtra(EXT_CUSTOMER_QUEUE_DETAIL, customerQueueId.toInt())
                        mLocalBroadcastManager.sendBroadcast(intt)
                        Timber.w("delete - customer-queue-id: $customerQueueId")
                    }
                }
            }
        } catch (ex: Exception) {
            Timber.e(ex)
        }
    }

    private fun getCustomerQueueDetail(customerQueueId: String, action: String){
        mDisposables.add(mDataManager.getCustomerQueueDetail(customerQueueId)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribeWith(object : DisposableSingleObserver<Response<ResponseData<CustomerQueueData>>>() {
                    override fun onSuccess(data: Response<ResponseData<CustomerQueueData>>) {
                        if (isDisposed) {
                            return
                        }
                        if(data.body() != null && data.body()!!.data != null) {
                            val table = data.body()!!.data
                            val intent = Intent(action)
                            intent.putExtra(EXT_CUSTOMER_QUEUE_DETAIL, table!!)
                            mLocalBroadcastManager.sendBroadcast(intent)
                        }

                        if (!isDisposed) {
                            dispose()
                        }
                    }

                    override fun onError(e: Throwable) {
                        Timber.d("Error get orders delivery $e")
                        if (!isDisposed) {
                            dispose()
                        }
                    }
                }))
    }

    private fun getOrderById(orderId: String, action: String) {

        if (mGetOrderDisposable != null && mGetOrderDisposable?.isDisposed == false)
            mGetOrderDisposable?.dispose()

        mGetOrderDisposable = mDataManager.getOrderDetail(orderId, "1") // need order detail data to fill driver name
                .map {
                    if (it.isSuccessful) {
                        val orderResponse = it.body()
                        val orderData = orderResponse?.data?.order
                        if (orderData != null) {
                            return@map orderData
                        }
                    }
                    return@map OrderedData("Error")
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<OrderedData>() {
                    override fun onSuccess(orderData: OrderedData) {
                        if (isDisposed) return

                        if (!orderData.isNull) {
                            // Send broadcast to OrderHistoryActivity
                            val intent = Intent(action)
                            intent.putExtra(EXT_ORDER_DETAIL, orderData)
                            mLocalBroadcastManager.sendBroadcast(intent)
                        }

                        if (!isDisposed) dispose()
                    }

                    override fun onError(e: Throwable) {
                        Timber.w("onError: ${e.message}")
                        if (isDisposed) return
                        if (!isDisposed) dispose()
                    }
                })

        if (mGetOrderDisposable != null) mDisposables.add(mGetOrderDisposable!!)
    }

    private fun pushToken(): JSONObject {
        // Sending an object
        val obj = JSONObject()
        obj.put("token", PreferenceManager.instance.userToken)
        return obj
    }

    override fun onDestroy() {
        mSocket?.disconnect()
        mDisposables.clear()
        mDisposables.dispose()
        super.onDestroy()

    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        mSocket?.disconnect()
        mDisposables.clear()
        mDisposables.dispose()
        Timber.d("Service stopped ${mSocket?.connected()}")
    }
}
