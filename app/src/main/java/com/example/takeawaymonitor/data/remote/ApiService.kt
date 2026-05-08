package com.example.takeawaymonitor.data.remote

import com.example.takeawaymonitor.data.remote.model.ConfigResponse
import com.example.takeawaymonitor.data.remote.model.PosConfigResponse
import com.example.takeawaymonitor.data.remote.model.AppVersionResponse
import retrofit2.http.GET
import retrofit2.http.Header

interface ApiService {
    @GET("system/v1/config")
    suspend fun getConfig(
        @Header("X-CLIENT-ID") clientId: String = "b39773b0-435b-4f41-80e9-163eef20e0ab"
    ): ConfigResponse

    @GET("system/config")
    suspend fun getPosConfig(): PosConfigResponse

    @GET("system/get_takeaway_monitor_application")
    suspend fun getAppVersion(): AppVersionResponse
}
