package com.phsmk.id.takeaway_monitor.data.remote.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.phsmk.id.takeaway_monitor.util.AppConfig
import com.phsmk.id.takeaway_monitor.util.Constants

data class PosConfigResponse(
    @SerializedName("data") val data: PosConfigData
)

data class PosConfigData(
    @SerializedName("UI")
    @Expose
    var uI: PosUI? = null,

    @SerializedName("order_status")
    var orderStatuses: List<OrderStatus>? = null,

    @SerializedName("outlet_name")
    @Expose
    var outletName: String? = null,

    @SerializedName("outlet_address")
    @Expose
    var outletAddress: String? = null,

    @SerializedName("njop_pajak")
    @Expose
    var njopPajak: String? = null,

    @SerializedName("outlet_code")
    @Expose
    var outletCode: String? = null,

    @SerializedName("outlet_phone")
    @Expose
    var outletPhone: String? = null,

    @SerializedName("outlet_lat")
    var outletLat: String? = null,

    @SerializedName("outlet_long")
    var outletLong: String? = null,

    @SerializedName("order_type")
    var orderTypes: List<OrderType>? = null,

    @SerializedName("order_device")
    var orderDevices: List<OrderDevice>? = null,

    @SerializedName("transaction_type")
    var transactionTypes: List<TransactionType>? = null,

    @SerializedName("payment_method")
    var paymentMethods: List<PaymentMethod>? = null,

    @SerializedName("partners")
    var partners: List<Partner>? = null,

    @SerializedName("category_addon")
    @Expose
    var categoryAddon: CategoryAddon? = null,

    @SerializedName("delivery_time")
    var deliveryTime: String? = null,

    @SerializedName("takeaway_time")
    var takeawayTime: String? = null,

    @SerializedName("training_mode")
    var trainingMode: String? = null,

    @SerializedName("reservation")
    var reservation: String? = null,

    @SerializedName("loyalty_point")
    var loyaltyPoint: String? = null,

    @SerializedName("greeting_msg")
    @Expose
    var greetingMsg: List<GreetingMsg>? = null,

    @SerializedName("roles")
    @Expose
    var roles: List<Role>? = null,

    @SerializedName("show_greeting")
    var show_greeting: String? = null,

    @SerializedName("show_eod")
    @Expose
    var showEOD: String? = null,

    @SerializedName("url_socket")
    @Expose
    var urlSocket: String? = null,

    @SerializedName("buzzer_ip")
    @Expose
    var buzzerIp: String? = null,

    @SerializedName("buzzer_device")
    @Expose
    var buzzerDevice: String? = null,

    @SerializedName("buzzer_serno")
    @Expose
    var buzzerSerno: String? = null,

    @SerializedName("delivery_fee")
    var delivery_fee: String? = null,

    @SerializedName("suggest_remark")
    var suggestRemarks: List<String>? = null,

    @SerializedName("is_display_donation")
    var isDisplayDonation: String? = null,

    @SerializedName("is_display_org_split_bill")
    var isDisplayOrgSplitBill: String? = null,

    @SerializedName("payment_online_ids")
    var payment_online_ids: List<Int>? = null,

    @SerializedName("payment_internal_ids")
    var payment_internal_ids: List<Int>? = null,

    @SerializedName("order_code_online_prefixs")
    var order_code_online_prefixs: List<Int>? = null,

    @SerializedName("order_code_event_prefix")
    var order_code_event_prefix: List<Int>? = null,

    @SerializedName("tax_rate")
    var tax_rate: String? = "0",

    @SerializedName("menu_version")
    var menu_version: String? = "0",

    @SerializedName("list_payment_expected_ids")
    var list_payment_expected_ids: List<String>? = null,

    @SerializedName("template_footer_print")
    var template_footer_print: String? = null,

    @SerializedName("is_display_empty_group")
    var isDisplayEmptyGroup: String? = null,

    @SerializedName("maximum_discount")
    var maximum_discount: String? = null,

    @SerializedName("group_by_pax")
    var listGroupByPax: List<List<Int>>? = null,

    @SerializedName("takeaway_ads")
    var takeaway_ads: Int = 0,

    @SerializedName("outlet_logo")
    var outlet_logo: String? = "",

    @SerializedName("outlet_logo_print")
    var outlet_logo_print: String? = "",

    @SerializedName("payment_method_for_delivery")
    var payment_method_for_delivery: List<PaymentMethod>? = null,

    @SerializedName("make_later")
    var make_later: String? = "",

    @SerializedName("store_branch")
    var store_branch: String? = "PHR",

    @SerializedName("bri_edc")
    var bri_edc: String? = "",

    @SerializedName("ip_bri_edc")
    var ip_bri_edc: String? = "",

    @SerializedName("port_bri_edc")
    var port_bri_edc: String? = "",

    @SerializedName("trans_type_bri")
    var trans_type_bri: List<TransTypeBRI>? = null,

    @SerializedName("is_void")
    var is_void: String? = "",

    @SerializedName("takeaway_fee")
    var takeaway_fee: List<TakeawayFee>? = null,

    @SerializedName("dinein_fee")
    var dinein_fee: String? = "",

    @SerializedName("menu_editor")
    var menu_editor: String? = "",

    @SerializedName("service_charge_label")
    var service_charge_label: ServiceChargeLabel? = null,

    @SerializedName("service_charge_status")
    var service_charge_status: ServiceChargeStatus? = null,

    @SerializedName("is_hide_print_bill")
    var isHidePrintBill: String? = null,

    @SerializedName("delivery")
    var delivery: DetailFee? = null,

    @SerializedName("takeaway")
    var takeaway: DetailFee? = null,

    @SerializedName("dinein")
    var dinein: DetailFee? = null,

    @SerializedName("gojek")
    var gojek: DetailFee? = null,

    @SerializedName("grab")
    var grab: DetailFee? = null,

    @SerializedName("maxim")
    var maxim: DetailFee? = null,

    @SerializedName("others")
    var others: DetailFee? = null,

    @SerializedName("shopee")
    var shopee: DetailFee? = null,

    @SerializedName("paylater_status")
    @Expose
    var paylater_status: PaylaterStatus? = null,

    var isAskPrinter1st: Boolean = false,
    var toppingAddOns: List<ToppingAddOn>? = null,
    var toppings: List<Topping>? = null,
    var additionToppingData: List<AdditionToppingData>? = null
) {
    fun isPHR(): Boolean = store_branch != null && store_branch.equals("PHR", ignoreCase = true)

    fun isPHD(): Boolean = store_branch != null && store_branch.equals("PHD", ignoreCase = true)

    fun isShowGreetingMsg(): Boolean = show_greeting != null && show_greeting == "1"

    fun isShowTakeawayAds(): Boolean = takeaway_ads == 1

    fun isVoid(): Boolean = is_void != null && is_void == "1"

    val outletLogo: String? get() = outlet_logo

    val storeBranch: String? get() = store_branch

    fun deliveryFee(): Double {
        return if (delivery != null) {
            delivery?.value ?: 0.0
        } else {
            delivery_fee?.toDoubleOrNull() ?: 0.0
        }
    }

    fun dineinFee(): Double {
        return if (dinein != null) {
            dinein?.value ?: 0.0
        } else {
            dinein_fee?.toDoubleOrNull() ?: 0.0
        }
    }

    fun takeawayFee(): List<TakeawayFee> {
        val takeawayTemp = mutableListOf<TakeawayFee>()
        if (takeaway != null && gojek != null && grab != null && shopee != null && maxim != null && others != null) {
            takeawayTemp.add(TakeawayFee("own_channel", takeaway?.code, takeaway?.value?.toString()))
            takeawayTemp.add(TakeawayFee("gojek", gojek?.code, gojek?.value?.toString()))
            takeawayTemp.add(TakeawayFee("grab", grab?.code, grab?.value?.toString()))
            takeawayTemp.add(TakeawayFee("shopee", shopee?.code, shopee?.value?.toString()))
            takeawayTemp.add(TakeawayFee("maxim", maxim?.code, maxim?.value?.toString()))
            takeawayTemp.add(TakeawayFee("others", others?.code, others?.value?.toString()))
        } else {
            takeaway_fee?.let { takeawayTemp.addAll(it) }
        }
        return takeawayTemp
    }
}

data class PosUI(
    @SerializedName("element") val element: UIElement?,
    @SerializedName("text") val text: UIText?,
    @SerializedName("banner") val banner: String?,
    @SerializedName("logo") val logo: String?,
    @SerializedName("background") val background: String?
)

data class UIElement(
    @SerializedName("main") val main: String?,
    @SerializedName("secondary") val secondary: String?,
    @SerializedName("sub_1") val sub1: String?,
    @SerializedName("sub_2") val sub2: String?,
    @SerializedName("sub_3") val sub3: String?,
    @SerializedName("main_background") val mainBackground: String?
)

data class UIText(
    @SerializedName("title") val title: String?,
    @SerializedName("dark") val dark: String?,
    @SerializedName("light") val light: String?
)

// Stubs for missing classes
class OrderStatus {
    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OrderStatus) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}
class OrderType(
    @SerializedName("id")
    @Expose
    var id: String? = null,
    @SerializedName("name")
    @Expose
    var name: String? = null
) {
    var isChoose: Boolean = false

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OrderType) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}
class OrderDevice(
    @SerializedName("id")
    @Expose
    var id: String? = null,
    @SerializedName("name")
    @Expose
    var name: String? = null
) {
    var isChoose: Boolean = false

    constructor() : this(null, null)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OrderDevice) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}
class TransactionType(
    @SerializedName("id")
    @Expose
    var id: String? = null,
    @SerializedName("name")
    @Expose
    var name: String? = null
) {
    var isChoose: Boolean = false

    constructor() : this(null, null)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TransactionType) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}
class Partner() : Parcelable {
    @SerializedName("id")
    var id: String? = null

    @SerializedName("name")
    var name: String? = null

    @SerializedName("code")
    var code: String? = "0"

    constructor(id: String?, name: String?) : this() {
        this.id = id
        this.name = name
    }

    protected constructor(source: Parcel) : this() {
        id = source.readString()
        name = source.readString()
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(id)
        dest.writeString(name)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Partner> {
        override fun createFromParcel(source: Parcel): Partner = Partner(source)
        override fun newArray(size: Int): Array<Partner?> = arrayOfNulls(size)
    }
}
class CategoryAddon {
    @SerializedName("id_ext")
    @Expose
    var idExt: Int? = null
}
data class GreetingMsg(
    @SerializedName("name")
    @Expose
    var name: String? = Constants.EMPTY,

    @SerializedName("message")
    @Expose
    var message: List<String>? = arrayListOf(),

    @SerializedName("is_random")
    @Expose
    var isRandom: String = "0",

    @SerializedName("is_active")
    @Expose
    var isActive: String? = "0",

    var currentPosition: Int = 0
) {
    fun isGreetingActive(): Boolean = isActive == "1"
    fun isRandomGreeting(): Boolean = isRandom == "1"
}
data class Role(
    @SerializedName("id")
    @Expose
    var id: String = Constants.EMPTY,

    @SerializedName("name")
    @Expose
    var name: String = Constants.EMPTY
)
data class TransTypeBRI(
    @SerializedName("id")
    @Expose
    var id: String? = Constants.EMPTY,

    @SerializedName("code")
    @Expose
    var code: Int? = 0,

    @SerializedName("trans_type")
    @Expose
    var trans_type: String = "0",

    @SerializedName("remark")
    @Expose
    var remark: String? = "0"
)
class TakeawayFee(
    @SerializedName("type") val type: String? = null,
    @SerializedName("code") val code: String? = null,
    @SerializedName("value") val value: String? = null
)
data class ServiceChargeLabel(
    @SerializedName("dine_in")
    @Expose
    var dineIn: String? = Constants.EMPTY,

    @SerializedName("takeaway")
    @Expose
    var takeaway: String? = Constants.EMPTY,

    @SerializedName("delivery")
    @Expose
    var delivery: String? = Constants.EMPTY
)
data class ServiceChargeStatus(
    @SerializedName("dine_in")
    @Expose
    var dineIn: Int? = 0,

    @SerializedName("takeaway")
    @Expose
    var takeaway: Int? = 0,

    @SerializedName("delivery")
    @Expose
    var delivery: Int? = 0
)
class DetailFee {
    @SerializedName("code") val code: String? = null
    @SerializedName("value") val value: Double? = null
}
data class PaylaterStatus(
    @SerializedName("dine_in")
    @Expose
    var dineIn: Int? = 0,

    @SerializedName("takeaway")
    @Expose
    var takeaway: Int? = 0,

    @SerializedName("delivery")
    @Expose
    var delivery: Int? = 0,

    @SerializedName("eat_in")
    @Expose
    var eatIn: Int? = 0
)
class ToppingAddOn(
    @SerializedName("id")
    @Expose
    var id: String? = Constants.EMPTY,

    @SerializedName("menu_id")
    @Expose
    var menu_id: String? = Constants.EMPTY,

    @SerializedName("topping_id")
    @Expose
    var toppingID: String? = Constants.EMPTY,

    @SerializedName("variant_id")
    @Expose
    var variantID: String? = Constants.EMPTY,

    @SerializedName("addon_id")
    @Expose
    var addonID: String? = Constants.EMPTY,

    @SerializedName("short_name")
    @Expose
    var shortName: String? = Constants.EMPTY,

    @SerializedName("thumbnail")
    @Expose
    var thumbNail: String? = Constants.EMPTY,

    @SerializedName("quantity")
    @Expose
    var quantity: Int = AppConfig.MIN_QUANTITY,

    @SerializedName("price")
    @Expose
    var price: Double = 0.0,

    @SerializedName("gojek_price")
    @Expose
    var priceGojek: Double = 0.0,

    @SerializedName("grab_price")
    @Expose
    var priceGrab: Double = 0.0,

    @SerializedName("others_price")
    @Expose
    var priceOthers: Double = 0.0,

    @SerializedName("plucode")
    @Expose
    var plucode: String? = Constants.EMPTY,

    @SerializedName("is_active")
    @Expose
    var isActive: Int = 1
) : Parcelable {

    var isChecked = false
    var defaultQuantity = AppConfig.MIN_QUANTITY

    constructor(name: String) : this() {
        this.shortName = name
    }

    fun remove(): Boolean {
        return true
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ToppingAddOn) return false

        return !shortName.isNullOrEmpty() && !other.shortName.isNullOrEmpty() && shortName == other.shortName
    }

    override fun hashCode(): Int {
        return shortName?.hashCode() ?: 0
    }

    fun equalsToCombine(other: ToppingAddOn): Boolean {
        if (this === other) return true
        return (toppingID == other.toppingID) && (addonID == other.addonID) && (price == other.price) &&
                (quantity == other.quantity) && (shortName == other.shortName)
    }

    protected constructor(source: Parcel) : this(
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readInt(),
        source.readDouble(),
        source.readDouble(),
        source.readDouble(),
        source.readDouble(),
        source.readString(),
        source.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(id)
        writeString(menu_id)
        writeString(toppingID)
        writeString(variantID)
        writeString(addonID)
        writeString(shortName)
        writeString(thumbNail)
        writeInt(quantity)
        writeDouble(price)
        writeDouble(priceGojek)
        writeDouble(priceGrab)
        writeDouble(priceOthers)
        writeString(plucode)
        writeInt(isActive)
    }

    fun clone(): ToppingAddOn {
        val result = ToppingAddOn()
        result.id = id
        result.menu_id = menu_id
        result.toppingID = toppingID
        result.variantID = variantID
        result.addonID = addonID
        result.shortName = shortName
        result.thumbNail = thumbNail
        result.quantity = quantity
        result.price = price
        result.priceGojek = priceGojek
        result.priceGrab = priceGrab
        result.priceOthers = priceOthers
        result.plucode = plucode
        result.isActive = isActive
        return result
    }

    fun convertToTopping(): Topping {
        return Topping(
            menu_id,
            toppingID,
            plucode,
            shortName,
            quantity,
            price,
            priceGojek,
            priceGrab,
            priceOthers
        )
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ToppingAddOn> = object : Parcelable.Creator<ToppingAddOn> {
            override fun createFromParcel(source: Parcel): ToppingAddOn = ToppingAddOn(source)
            override fun newArray(size: Int): Array<ToppingAddOn?> = arrayOfNulls(size)
        }
    }
}
data class Topping(
    @SerializedName("menu_id")
    @Expose
    var menuID: String? = Constants.EMPTY,

    @SerializedName("id")
    @Expose
    var id: String? = Constants.EMPTY,

    @SerializedName("plucode")
    @Expose
    var plucode: String? = Constants.EMPTY,

    @SerializedName("name")
    @Expose
    var name: String? = Constants.EMPTY,

    @SerializedName("quantity")
    @Expose
    var quantity: Int = AppConfig.MIN_QUANTITY,

    @SerializedName("price")
    @Expose
    var price: Double = 0.0,

    @SerializedName("gojek_price")
    @Expose
    var priceGojek: Double = 0.0,

    @SerializedName("grab_price")
    @Expose
    var priceGrab: Double = 0.0,

    @SerializedName("others_price")
    @Expose
    var priceOthers: Double = 0.0
) : Parcelable {

    var isChecked = false
    var defaultQuantity = AppConfig.MIN_QUANTITY

    constructor(name: String) : this() {
        this.name = name
    }

    fun remove(): Boolean {
        return true
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Topping) return false

        return !name.isNullOrEmpty() && !other.name.isNullOrEmpty() && name == other.name
    }

    override fun hashCode(): Int {
        return name?.hashCode() ?: 0
    }

    protected constructor(source: Parcel) : this(
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readInt(),
        source.readDouble(),
        source.readDouble(),
        source.readDouble(),
        source.readDouble()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(menuID)
        writeString(id)
        writeString(plucode)
        writeString(name)
        writeInt(quantity)
        writeDouble(price)
        writeDouble(priceGojek)
        writeDouble(priceGrab)
        writeDouble(priceOthers)
    }

    fun clone(): Topping {
        val result = Topping()
        result.menuID = menuID
        result.id = id
        result.plucode = plucode
        result.name = name
        result.quantity = quantity
        result.defaultQuantity = defaultQuantity
        result.price = price
        result.priceGojek = priceGojek
        result.priceGrab = priceGrab
        result.priceOthers = priceOthers
        return result
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Topping> = object : Parcelable.Creator<Topping> {
            override fun createFromParcel(source: Parcel): Topping = Topping(source)
            override fun newArray(size: Int): Array<Topping?> = arrayOfNulls(size)
        }
    }
}
data class AdditionToppingData(
    @SerializedName("id")
    @Expose
    var id: String? = Constants.EMPTY,

    @SerializedName("topping_id")
    @Expose
    var toppingId: String? = Constants.EMPTY,

    @SerializedName("variant_id")
    @Expose
    var variantId: String? = Constants.EMPTY,

    @SerializedName("addon_id")
    @Expose
    var addonId: String? = Constants.EMPTY,

    @SerializedName("short_name")
    @Expose
    var shortName: String? = Constants.EMPTY,

    @SerializedName("thumbnail")
    @Expose
    var thumbnail: String? = Constants.EMPTY,

    @SerializedName("price")
    @Expose
    var price: String? = Constants.EMPTY,

    @SerializedName("gojek_price")
    @Expose
    var gojekPrice: String? = Constants.EMPTY,

    @SerializedName("grab_price")
    @Expose
    var grabPrice: String? = Constants.EMPTY,

    @SerializedName("others_price")
    @Expose
    var othersPrice: String? = Constants.EMPTY,

    @SerializedName("plucode")
    @Expose
    var plucode: String? = Constants.EMPTY,

    @SerializedName("is_active")
    @Expose
    var isActive: String? = Constants.EMPTY,

    var isNull: Boolean = false,
    var errorMsg: String? = null
) {
    constructor(errorMsg: String?) : this() {
        isNull = true
        this.errorMsg = errorMsg
    }
}
