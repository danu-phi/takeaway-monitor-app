package com.phsmk.id.takeaway_monitor.data.remote.model

import com.google.gson.annotations.SerializedName

data class AppVersionResponse(
    @SerializedName("data") val data: VersionData?
)

data class VersionData(
    @SerializedName("version")
    val version: String = "",

    @SerializedName("path_file")
    val path_file: String = "",

    @SerializedName("description")
    val description: String = "",

    @SerializedName("created_date")
    val createdDate: String = "",

    @SerializedName("updated_date")
    val updatedDate: String = ""
)
