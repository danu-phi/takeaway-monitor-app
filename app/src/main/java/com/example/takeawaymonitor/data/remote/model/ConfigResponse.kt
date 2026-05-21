package com.example.takeawaymonitor.data.remote.model

import com.google.gson.annotations.SerializedName

data class ConfigResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("data") val data: ConfigData,
    @SerializedName("message") val message: String?
)

data class ConfigData(
    @SerializedName("account_is_active") val accountIsActive: String?,
    @SerializedName("allowed_domain") val allowedDomain: String?,
    @SerializedName("app_icon") val appIcon: AppIcon?,
    @SerializedName("available_date") val availableDate: String?,
    @SerializedName("business_hours") val businessHours: BusinessHours?,
    @SerializedName("footer_key") val footerKey: String?,
    @SerializedName("rounding") val rounding: String?,
    @SerializedName("group_by_pax") val listGroupByPax: List<List<Int>>? = null,
    @SerializedName("isDisplayEmptyGroup") val isDisplayEmptyGroup: String? = null
)

data class AppIcon(
    @SerializedName("primary_logo") val primaryLogo: Logo?,
    @SerializedName("splash_logo") val splashLogo: Logo?
)

data class Logo(
    @SerializedName("color") val color: String?,
    @SerializedName("monochrome") val monochrome: String?
)

data class BusinessHours(
    @SerializedName("closing_time") val closingTime: String?,
    @SerializedName("interval") val interval: String?,
    @SerializedName("opening_time") val openingTime: String?
)
