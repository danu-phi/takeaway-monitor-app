package com.phsmk.id.takeaway_monitor.data.remote

import com.phsmk.id.takeaway_monitor.data.remote.model.*
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import retrofit2.http.Url

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
    suspend fun downloadFile(@Url url: String): Response<okhttp3.ResponseBody>

    // RxJava methods for PushService
    @GET("customer/customer_queue")
    fun getCustomerQueueDetailRx(@Query("customer_queue_id") id: String): Single<Response<ResponseData<CustomerQueueData>>>

    @GET("order/detail")
    fun getOrderDetailRx(@Query("order_id") orderId: String, @Query("has_detail") hasDetail: String): Single<Response<OrderDetailResponse>>
}
