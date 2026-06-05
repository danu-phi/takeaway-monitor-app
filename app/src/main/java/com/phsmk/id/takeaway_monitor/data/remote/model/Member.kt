package com.phsmk.id.takeaway_monitor.data.remote.model

import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.phsmk.id.takeaway_monitor.util.Constants
import java.util.Date

data class Member(
    @SerializedName("id")
    @Expose
    var id: String? = Constants.EMPTY,

    @SerializedName("email")
    @Expose
    var email: String? = Constants.EMPTY,

    @SerializedName("name")
    @Expose
    var name: String? = Constants.EMPTY,

    @SerializedName("phone")
    @Expose
    var phone: String? = Constants.EMPTY,

    @SerializedName("alternative_phone")
    @Expose
    var alternatePhone: String? = Constants.EMPTY,

    @SerializedName("address")
    @Expose
    var address: String? = Constants.EMPTY,

    @SerializedName("zone")
    var zone: String? = null,

    @SerializedName("unit_no")
    @Expose
    var unitNo: String? = Constants.EMPTY,

    @SerializedName("building")
    @Expose
    var building: String? = Constants.EMPTY,

    @SerializedName("note")
    @Expose
    var note: String? = Constants.EMPTY,

    @SerializedName("gender")
    @Expose
    var gender: String? = Constants.EMPTY,

    @SerializedName("birthday")
    @Expose
    var birthday: Date? = null,

    @SerializedName("created_by")
    @Expose
    var createdBy: String? = Constants.EMPTY,

    @SerializedName("updated_by")
    @Expose
    var updatedBy: String? = Constants.EMPTY,

    @SerializedName("created_date")
    @Expose
    var createdDate: String? = Constants.EMPTY,

    @SerializedName("updated_date")
    @Expose
    var updatedDate: String? = Constants.EMPTY,

    var isNull: Boolean = false,

    var mErrorMsg: String = Constants.EMPTY,

    @SerializedName("list_address")
    @Expose
    var listAddresses: MutableList<SavedAddress> = mutableListOf(),

    @SerializedName("point")
    var point: String? = null,

    @SerializedName("loyalty_point")
    var loyalty_point: String? = null,

    @SerializedName("membership")
    var membership: String? = null,

    var addressId: String? = null
) : Parcelable {
    constructor(error: String) : this() {
        isNull = true
        this.mErrorMsg = error
    }

    constructor(
        phone: String,
        email: String,
        name: String,
        address: String,
        zone: String,
        unitNo: String,
        building: String
    ) : this() {
        this.phone = phone
        this.email = email
        this.name = name
        this.address = address
        this.unitNo = unitNo
        this.building = building
        this.zone = zone
    }

    constructor(
        phone: String,
        alternatePhone: String,
        email: String,
        name: String,
        address: String,
        zone: String,
        unitNo: String,
        building: String
    ) : this() {
        this.phone = phone
        this.alternatePhone = alternatePhone
        this.email = email
        this.name = name
        this.address = address
        this.unitNo = unitNo
        this.building = building
        this.zone = zone
    }

    fun getFullAddress(): String {
        val builder = StringBuilder()

        if (!TextUtils.isEmpty(address)) {
            builder.append(address)
        }

        if (!TextUtils.isEmpty(building)) {
            if (!TextUtils.isEmpty(builder.toString())) {
                builder.append(", ")
            }
            builder.append(building)
        }

        if (!TextUtils.isEmpty(unitNo)) {
            builder.append(" No.")
            builder.append(unitNo)
        }

        return builder.toString()
    }

    fun mapToAddressResult(): AddressResult {
        val addressResult = AddressResult()
        addressResult.street = address
        addressResult.zone = zone
        return addressResult
    }

    fun pointValue(): Int {
        return if (!TextUtils.isEmpty(point) && point?.toIntOrNull() != null) point!!.toInt() else 0
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
        source.readSerializable() as Date?,
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        1 == source.readInt(),
        source.readString().toString(),
        source.createTypedArrayList(SavedAddress.CREATOR) ?: mutableListOf(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(id)
        writeString(email)
        writeString(name)
        writeString(phone)
        writeString(alternatePhone)
        writeString(address)
        writeString(zone)
        writeString(unitNo)
        writeString(building)
        writeString(note)
        writeString(gender)
        writeSerializable(birthday)
        writeString(createdBy)
        writeString(updatedBy)
        writeString(createdDate)
        writeString(updatedDate)
        writeInt((if (isNull) 1 else 0))
        writeString(mErrorMsg)
        writeTypedList(listAddresses)
        writeString(point)
        writeString(loyalty_point)
        writeString(membership)
        writeString(addressId)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Member> = object : Parcelable.Creator<Member> {
            override fun createFromParcel(source: Parcel): Member = Member(source)
            override fun newArray(size: Int): Array<Member?> = arrayOfNulls(size)
        }
    }
}

class SavedAddress() : Parcelable {
    constructor(parcel: Parcel) : this()
    override fun writeToParcel(parcel: Parcel, flags: Int) {}
    override fun describeContents(): Int = 0
    companion object CREATOR : Parcelable.Creator<SavedAddress> {
        override fun createFromParcel(parcel: Parcel): SavedAddress = SavedAddress(parcel)
        override fun newArray(size: Int): Array<SavedAddress?> = arrayOfNulls(size)
    }
}

class AddressResult {
    var street: String? = null
    var zone: String? = null
}
