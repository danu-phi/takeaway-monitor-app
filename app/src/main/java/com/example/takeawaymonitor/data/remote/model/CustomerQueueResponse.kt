package com.example.takeawaymonitor.data.remote.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CustomerQueueResponse(
    @SerializedName("data")
    @Expose
    val data: List<CustomerQueueData>? = null,
    
    @SerializedName("total")
    @Expose
    val total: Int? = null
)

data class CustomerQueueData(
    @SerializedName("id")
    @Expose
    var id: String? = null,

    @SerializedName("customer_name")
    @Expose
    var customerName: String? = null,

    @SerializedName("phone")
    @Expose
    var phone: String? = null,

    @SerializedName("email")
    @Expose
    var email: String? = null,

    @SerializedName("note")
    @Expose
    var note: String? = null,

    @SerializedName("capacity")
    @Expose
    var capacity: String? = null,

    @SerializedName("table_id")
    @Expose
    var tableId: String? = null,

    @SerializedName("is_done")
    @Expose
    var isDone: String? = null,

    @SerializedName("is_order")
    @Expose
    var isOrder: String? = null,

    @SerializedName("is_delete")
    @Expose
    var isDelete: String? = null,

    @SerializedName("created_by")
    @Expose
    var createdBy: String? = null,

    @SerializedName("updated_by")
    @Expose
    var updatedBy: String? = null,

    @SerializedName("created_date")
    @Expose
    var createdDate: String? = null,

    @SerializedName("updated_date")
    @Expose
    var updatedDate: String? = null,

    @SerializedName("order_id")
    @Expose
    var orderId: String? = null,

    @SerializedName("amount")
    @Expose
    var amount: String? = null,

    @SerializedName("total_quantity")
    @Expose
    var totalQuantity: Int? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(customerName)
        parcel.writeString(phone)
        parcel.writeString(email)
        parcel.writeString(note)
        parcel.writeString(capacity)
        parcel.writeString(tableId)
        parcel.writeString(isDone)
        parcel.writeString(isOrder)
        parcel.writeString(isDelete)
        parcel.writeString(createdBy)
        parcel.writeString(updatedBy)
        parcel.writeString(createdDate)
        parcel.writeString(updatedDate)
        parcel.writeString(orderId)
        parcel.writeString(amount)
        parcel.writeValue(totalQuantity)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<CustomerQueueData> {
        override fun createFromParcel(parcel: Parcel): CustomerQueueData = CustomerQueueData(parcel)
        override fun newArray(size: Int): Array<CustomerQueueData?> = arrayOfNulls(size)
    }
}
