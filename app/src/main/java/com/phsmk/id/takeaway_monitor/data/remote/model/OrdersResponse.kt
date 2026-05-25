package com.phsmk.id.takeaway_monitor.data.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class OrdersResponse(
    @SerializedName("data")
    @Expose
    var orderData: Orders? = null
)

class Orders {
    @SerializedName("orders")
    @Expose
    var orderDetail: List<OrderedData>? = null
}

data class OrderedData(
    @SerializedName("id")
    @Expose
    var id: String? = "",

    @SerializedName("code")
    @Expose
    var code: String? = "",

    @SerializedName("order_status_id")
    @Expose
    var orderStatusId: String? = "",

    @SerializedName("first_name")
    @Expose
    var firstName: String? = "",

    @SerializedName("last_name")
    @Expose
    var lastName: String? = "",

    @SerializedName("created_date")
    @Expose
    var createdDate: String? = "",

    @SerializedName("updated_date")
    @Expose
    var updatedDate: String? = "",

    @SerializedName("details")
    @Expose
    var details: List<OrderDetail>? = null,

    @SerializedName("capacity")
    @Expose
    var capacity: String? = "0",

    var orderNo: Int = 0,

    @SerializedName("name")
    @Expose
    var name: String? = "",
)

data class OrderDetail(
    @SerializedName("id")
    @Expose
    val id: String?,
    @SerializedName("name")
    @Expose
    val name: String?,
    @SerializedName("quantity")
    @Expose
    val quantity: Int?,
    @SerializedName("price")
    @Expose
    val price: Double?
)
