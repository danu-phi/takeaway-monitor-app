package com.example.takeawaymonitor.data.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class MediaResponse(
    @SerializedName("data")
    @Expose
    val data: MediaData? = null
)

data class MediaData(
    @SerializedName("ads")
    @Expose
    var ads: List<Ad> = emptyList(),

    @SerializedName("timestamp")
    @Expose
    var timestamp: String? = null
)

data class Ad(
    @SerializedName("url")
    @Expose
    val url: String? = null,
    
    @SerializedName("type")
    @Expose
    val type: Int? = null, // Changed from String to Int based on "type": 1
    
    @SerializedName("loop")
    @Expose
    val loop: String? = null,

    var localPath: String? = null
)
