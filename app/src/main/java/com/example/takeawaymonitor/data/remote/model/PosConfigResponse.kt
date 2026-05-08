package com.example.takeawaymonitor.data.remote.model

import com.google.gson.annotations.SerializedName

data class PosConfigResponse(
    @SerializedName("data") val data: PosConfigData
)

data class PosConfigData(
    @SerializedName("UI") val ui: PosUI?,
    @SerializedName("outlet_code") val outletCode: String?,
    @SerializedName("outlet_name") val outletName: String?,
    @SerializedName("outlet_address") val outletAddress: String?,
    @SerializedName("outlet_phone") val outletPhone: String?,
    @SerializedName("outlet_lat") val outletLat: String?,
    @SerializedName("outlet_long") val outletLong: String?,
    @SerializedName("delivery_time") val deliveryTime: String?,
    @SerializedName("takeaway_time") val takeawayTime: String?,
    @SerializedName("store_branch") val storeBranch: String?,
    @SerializedName("outlet_logo") val outletLogo: String?,
    @SerializedName("store_type") val storeType: String?,
    @SerializedName("tax_rate") val taxRate: String?
)

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
