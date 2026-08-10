package com.phsmk.id.takeaway_monitor.data.remote

import com.google.gson.Gson
import com.phsmk.id.takeaway_monitor.data.remote.model.PosConfigResponse
import com.phsmk.id.takeaway_monitor.util.Utils
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebSocketManager @Inject constructor(
    private val gson: Gson
) {
    private var mSocket: Socket? = null

    private val _onConfigReceived = MutableSharedFlow<PosConfigResponse>(replay = 1)
    val onConfigReceived: SharedFlow<PosConfigResponse> = _onConfigReceived.asSharedFlow()

    private val _connectionStatus = MutableSharedFlow<Boolean>()
    val connectionStatus: SharedFlow<Boolean> = _connectionStatus.asSharedFlow()

    fun connect(url: String) {
        if (mSocket?.connected() == true) {
            Timber.d("Socket already connected")
            return
        }

        try {
            val okHttpClient = Utils.getUnsafeOkHttpClient()
            val options = IO.Options().apply {
                callFactory = okHttpClient
                webSocketFactory = okHttpClient
                forceNew = true
                reconnection = true
                timeout = 10000
                transports = arrayOf("websocket")
            }

            mSocket = IO.socket(url, options)
            mSocket?.let { socket ->
                socket.on(Socket.EVENT_CONNECT) {
                    Timber.d("WebSocket connected to $url")
                    _connectionStatus.tryEmit(true)
                }.on("resInitData") { args ->
                    if (args.isNotEmpty() && args[0] is JSONObject) {
                        try {
                            val jsonObject = args[0] as JSONObject
                            val configResponse = gson.fromJson(jsonObject.toString(), PosConfigResponse::class.java)
                            Timber.d("Received resInitData from WebSocket")
                            _onConfigReceived.tryEmit(configResponse)
                        } catch (e: Exception) {
                            Timber.e(e, "Error parsing resInitData")
                        }
                    }
                }.on(Socket.EVENT_CONNECT_ERROR) { args ->
                    val error = if (args.isNotEmpty()) args[0] else "Unknown error"
                    Timber.e("WebSocket connection error: $error")
                    _connectionStatus.tryEmit(false)
                }.on(Socket.EVENT_DISCONNECT) {
                    Timber.d("WebSocket disconnected")
                    _connectionStatus.tryEmit(false)
                }
                socket.connect()
            }
        } catch (e: Exception) {
            Timber.e(e, "Error initializing WebSocket")
        }
    }

    fun disconnect() {
        mSocket?.disconnect()
        mSocket = null
    }

    fun isConnected(): Boolean = mSocket?.connected() ?: false
}
