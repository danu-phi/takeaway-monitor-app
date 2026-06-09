package com.phsmk.id.takeaway_monitor.data.remote.model

import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.phsmk.id.takeaway_monitor.util.Constants

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

class OrderedData(

    @SerializedName("id")
    @Expose
    var id: String? = Constants.EMPTY,

    @SerializedName("code")
    @Expose
    var code: String? = Constants.EMPTY,

    @SerializedName("customer_id")
    @Expose
    var memberId: String? = Constants.EMPTY,

    @SerializedName("order_status_id")
    @Expose
    var orderStatusId: String? = Constants.EMPTY,

    @SerializedName("order_type_id")
    @Expose
    var orderTypeId: String? = Constants.EMPTY,

    @SerializedName("order_device_id")
    @Expose
    var orderDeviceId: String? = Constants.EMPTY,

    @SerializedName("is_complete")
    @Expose
    var isComplete: String? = Constants.EMPTY,

    @SerializedName("name")
    @Expose
    var name: String? = Constants.EMPTY,

    @SerializedName("first_name")
    @Expose
    var firstName: String? = Constants.EMPTY,

    @SerializedName("last_name")
    @Expose
    var lastName: String? = Constants.EMPTY,

    @SerializedName("email")
    @Expose
    var email: String? = Constants.EMPTY,

    @SerializedName("phone")
    @Expose
    var phone: String? = Constants.EMPTY,

    @SerializedName("address")
    @Expose
    var address: String? = Constants.EMPTY,

    @SerializedName("unit_no")
    var unitNo: String? = Constants.EMPTY,

    @SerializedName("building")
    var building: String? = Constants.EMPTY,

    @SerializedName("note")
    @Expose
    var note: String? = Constants.EMPTY,

    @SerializedName("device_type")
    @Expose
    var deviceType: String? = Constants.EMPTY,

    @SerializedName("amount")
    @Expose
    var amount: String? = Constants.EMPTY,

    @SerializedName("discount")
    @Expose
    var discount: String? = Constants.EMPTY,

    @SerializedName("tax_value")
    @Expose
    var taxValue: String? = Constants.EMPTY,

    @SerializedName("voucher_value")
    @Expose
    var voucherValue: String? = Constants.EMPTY,

    @SerializedName("is_paid")
    @Expose
    var isPaid: String? = Constants.EMPTY,

    @SerializedName("created_by")
    @Expose
    var createdBy: String? = Constants.EMPTY,

//        @SerializedName("updated_by")
//        @Expose
//        var updatedBy: Any? = null,

    @SerializedName("created_date")
    @Expose
    var createdDate: String? = Constants.EMPTY,

    @SerializedName("updated_date")
    @Expose
    var updatedDate: String? = Constants.EMPTY,

    @SerializedName("collection_time")
    @Expose
    var quoteTime: String? = Constants.EMPTY,

    @SerializedName("customer")
    @Expose
    var customer: Member? = null,

    @SerializedName("details")
    @Expose
    var details: List<OrderDetail>? = null,

    @SerializedName("payments")
    @Expose
    var payments: List<PaymentMethod>? = null,

    @SerializedName("vouchers")
    @Expose
    var vouchers: List<Voucher>? = null,

    var isEditOrder: Boolean? = false,

    @SerializedName("previous_status_id")
    @Expose
    var previousStatusId: String? = Constants.EMPTY,

    @SerializedName("partner_id")
    @Expose
    var partnerId: String? = Constants.EMPTY,

    @SerializedName("partner_code")
    @Expose
    var partnerCode: String? = Constants.EMPTY,

    @SerializedName("driver_name")
    @Expose
    var driverName: String? = null,

    @SerializedName("is_advance")
    @Expose
    var isAdvance: String? = Constants.EMPTY,

    @SerializedName("printered")
    @Expose
    var printered: String? = Constants.EMPTY,

    @SerializedName("created_by_name")
    @Expose
    var createdByName: String? = Constants.EMPTY,

    @SerializedName("encash_by_name")
    @Expose
    var encashByName: String? = Constants.EMPTY,

    var orderNo: Int = 0,

    var mErrorMsg: String = Constants.EMPTY,

    var isNull: Boolean = false,

    var errorMsg: String? = null

) : BaseLoading(), Parcelable {

    constructor(errorMsg: String?) : this() {
        isNull = true
        this.errorMsg = errorMsg
    }

    fun getFullName(): String {

        var fullName = Constants.EMPTY
        fullName += this.firstName

        if (!TextUtils.isEmpty(this.lastName)) {
            fullName += " "
            fullName += this.lastName
        }

        return fullName
    }

    fun isCheckPaid(): Boolean {
        return !TextUtils.isEmpty(isPaid) && isPaid?.toIntOrNull() == 1
    }

    constructor(source: Parcel) : this(
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readParcelable<Member>(Member::class.java.classLoader),
        source.createTypedArrayList(OrderDetail.CREATOR),
        source.createTypedArrayList(PaymentMethod.CREATOR),
        source.createTypedArrayList(Voucher.CREATOR),
        source.readValue(Boolean::class.java.classLoader) as Boolean?,
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(id)
        writeString(code)
        writeString(memberId)
        writeString(orderStatusId)
        writeString(orderTypeId)
        writeString(orderDeviceId)
        writeString(isComplete)
        writeString(name)
        writeString(firstName)
        writeString(lastName)
        writeString(email)
        writeString(phone)
        writeString(address)
        writeString(unitNo)
        writeString(building)
        writeString(note)
        writeString(deviceType)
        writeString(amount)
        writeString(discount)
        writeString(taxValue)
        writeString(voucherValue)
        writeString(isPaid)
        writeString(createdBy)
        writeString(createdDate)
        writeString(updatedDate)
        writeString(quoteTime)
        writeParcelable(customer, 0)
        writeTypedList(details)
        writeTypedList(payments)
        writeTypedList(vouchers)
        writeValue(isEditOrder)
        writeString(previousStatusId)
        writeString(partnerId)
        writeString(partnerCode)
        writeString(driverName)
        writeString(isAdvance)
        writeString(printered)
        writeString(createdByName)
        writeString(encashByName)
        writeInt(orderNo)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<OrderedData> = object : Parcelable.Creator<OrderedData> {
            override fun createFromParcel(source: Parcel): OrderedData = OrderedData(source)
            override fun newArray(size: Int): Array<OrderedData?> = arrayOfNulls(size)
        }
    }
}

data class OrderDetail(
    @SerializedName("id")
    @Expose
    val id: String?,
    @SerializedName("name")
    @Expose
    val name: String?,
    @SerializedName("full_name")
    @Expose
    val fullName: String?,
    @SerializedName("short_name")
    @Expose
    val shortName: String?,
    @SerializedName("menu_name")
    @Expose
    val menuName: String?,
    @SerializedName("quantity")
    @Expose
    val quantity: String?,
    @SerializedName("price")
    @Expose
    val price: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(fullName)
        parcel.writeString(shortName)
        parcel.writeString(menuName)
        parcel.writeString(quantity)
        parcel.writeString(price)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<OrderDetail> {
        override fun createFromParcel(parcel: Parcel): OrderDetail = OrderDetail(parcel)
        override fun newArray(size: Int): Array<OrderDetail?> = arrayOfNulls(size)
    }
}
