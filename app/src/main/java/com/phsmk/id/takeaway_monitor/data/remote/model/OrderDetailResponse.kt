package com.phsmk.id.takeaway_monitor.data.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class OrderDetailResponse(
    @SerializedName("data")
    @Expose
    var order: Orders? = null
)
