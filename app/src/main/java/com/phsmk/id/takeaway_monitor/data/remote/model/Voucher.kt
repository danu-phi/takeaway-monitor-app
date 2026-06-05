package com.phsmk.id.takeaway_monitor.data.remote.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Voucher() : Parcelable {
    @SerializedName("id")
    var id: String? = null

    @SerializedName("code")
    @Expose
    var code: String? = null

    @SerializedName("value")
    @Expose
    var value: String? = null

    @SerializedName("order_id")
    @Expose
    var orderId: String? = null

    @SerializedName("serial_code")
    @Expose
    var serialCode: String? = null

    @SerializedName("remark")
    @Expose
    var remark: String? = null

    @SerializedName("voucher_type_id")
    var voucherTypeId: Int? = null

    constructor(code: String?, serialCode: String?, value: String?) : this() {
        this.code = code
        this.serialCode = serialCode
        this.value = value
    }

    protected constructor(source: Parcel) : this() {
        id = source.readString()
        code = source.readString()
        value = source.readString()
        orderId = source.readString()
        serialCode = source.readString()
        remark = source.readString()
        voucherTypeId = source.readValue(Int::class.java.classLoader) as? Int
    }

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(id)
        dest.writeString(code)
        dest.writeString(value)
        dest.writeString(orderId)
        dest.writeString(serialCode)
        dest.writeString(remark)
        dest.writeValue(voucherTypeId)
    }

    companion object CREATOR : Parcelable.Creator<Voucher> {
        override fun createFromParcel(source: Parcel): Voucher = Voucher(source)
        override fun newArray(size: Int): Array<Voucher?> = arrayOfNulls(size)
    }
}
