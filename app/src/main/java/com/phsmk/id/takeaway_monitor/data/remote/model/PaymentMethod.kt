package com.phsmk.id.takeaway_monitor.data.remote.model

import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PaymentMethod() : Parcelable {
    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("level_1")
    @Expose
    var level1: String? = null

    @SerializedName("level_2")
    @Expose
    var level2: String? = null

    @SerializedName("value")
    @Expose
    var value: Double = 0.0

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("order_id")
    @Expose
    var orderId: String? = null

    @SerializedName("configuration")
    @Expose
    var configuration: Configuration? = null

    @SerializedName("is_hide")
    @Expose
    var isHide: String? = null

    fun isOCPaymentMethod(): Boolean {
        return (!TextUtils.isEmpty(id) && id?.toIntOrNull() == 22) ||
                (!TextUtils.isEmpty(level1) && level1.equals("OC", ignoreCase = true)) ||
                (!TextUtils.isEmpty(level2) && level2.equals("OC", ignoreCase = true))
    }

    fun isOutletMeals(): Boolean {
        return (!TextUtils.isEmpty(id) && id?.toIntOrNull() == 100) ||
                (!TextUtils.isEmpty(level1) && (level1.equals("MEAL-OUTLET", ignoreCase = true) ||
                        level1.equals("MEALS OUTLET", ignoreCase = true) ||
                        level1.equals("MEAL OUTLET", ignoreCase = true)))
    }

    constructor(id: String?, level1: String?, level2: String?) : this() {
        this.id = id
        this.level1 = level1
        this.level2 = level2
    }

    constructor(id: String?, value: Double, level2: String?) : this() {
        this.id = id
        this.value = value
        this.level2 = level2
    }

    protected constructor(source: Parcel) : this() {
        id = source.readString()
        level1 = source.readString()
        level2 = source.readString()
        value = source.readDouble()
        name = source.readString()
        orderId = source.readString()
        isHide = source.readString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PaymentMethod) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(id)
        dest.writeString(level1)
        dest.writeString(level2)
        dest.writeDouble(value)
        dest.writeString(name)
        dest.writeString(orderId)
        dest.writeString(isHide)
    }

    companion object CREATOR : Parcelable.Creator<PaymentMethod> {
        override fun createFromParcel(source: Parcel): PaymentMethod = PaymentMethod(source)
        override fun newArray(size: Int): Array<PaymentMethod?> = arrayOfNulls(size)
    }
}

class Configuration() : Parcelable {
    constructor(parcel: Parcel) : this()
    override fun writeToParcel(parcel: Parcel, flags: Int) {}
    override fun describeContents(): Int = 0
    companion object CREATOR : Parcelable.Creator<Configuration> {
        override fun createFromParcel(parcel: Parcel): Configuration = Configuration(parcel)
        override fun newArray(size: Int): Array<Configuration?> = arrayOfNulls(size)
    }
}
