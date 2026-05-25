package com.phsmk.id.takeaway_monitor.data.remote.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class SystemTimeResponse(
    @SerializedName("data") val data: SystemTimeData?
)

data class SystemTimeData(
    @SerializedName("datetime")
    var datetime: String? = null,
    @SerializedName("year")
    var year: String? = null,
    @SerializedName("month")
    var month: String? = null,
    @SerializedName("day")
    var day: String? = null,
    @SerializedName("hour")
    var hour: String? = null,
    @SerializedName("minute")
    var minute: String? = null,
    @SerializedName("second")
    var second: String? = null,

    var systemTime: Date? = null,

    var isNull: Boolean = false
) {
    constructor(isNull: Boolean) : this() {
        this.isNull = isNull
    }
}
