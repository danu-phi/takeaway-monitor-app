package com.phsmk.id.takeaway_monitor.data.remote

import com.phsmk.id.takeaway_monitor.data.remote.model.ConfigResponse
import com.phsmk.id.takeaway_monitor.data.remote.model.PosConfigResponse
import com.phsmk.id.takeaway_monitor.data.remote.model.AppVersionResponse
import com.phsmk.id.takeaway_monitor.data.remote.model.MediaResponse
import com.phsmk.id.takeaway_monitor.data.remote.model.OrdersResponse
import com.phsmk.id.takeaway_monitor.data.remote.model.SystemTimeResponse
import com.phsmk.id.takeaway_monitor.data.remote.model.CustomerQueueResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ApiService {
    @GET("system/v1/config")
    suspend fun getConfig(
        @Header("X-CLIENT-ID") clientId: String = "b39773b0-435b-4f41-80e9-163eef20e0ab"
    ): ConfigResponse

    @GET("system/config")
    suspend fun getPosConfig(): PosConfigResponse

    @GET("system/get_takeaway_monitor_application")
    suspend fun getAppVersion(): AppVersionResponse

    @GET("system/get_time")
    suspend fun getTime(): SystemTimeResponse

    @GET("system/list_ads_monitor")
    suspend fun getAdsMonitor(): MediaResponse

    @GET("order/list_takeaway_monitor")
    suspend fun getOrders(): OrdersResponse

    @GET("customer/list_customer_queue")
    suspend fun getCustomerQueue(): CustomerQueueResponse

    @retrofit2.http.Streaming
    @GET
    suspend fun downloadFile(@retrofit2.http.Url url: String): retrofit2.Response<okhttp3.ResponseBody>
}
