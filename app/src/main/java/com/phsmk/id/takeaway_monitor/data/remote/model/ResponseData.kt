package com.phsmk.id.takeaway_monitor.data.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ResponseData<T>(
    @SerializedName("code")
    @Expose
    val code: Int? = null,

    @SerializedName("data")
    @Expose
    val data: T? = null,

    @SerializedName("message")
    @Expose
    val message: String? = null
)
