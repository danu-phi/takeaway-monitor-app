package com.example.takeawaymonitor.util

import android.content.Context
import android.content.pm.PackageManager

object Utils {
    fun getVersion(context: Context): String {
        return try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            pInfo.versionName ?: "1.0.0"
        } catch (e: PackageManager.NameNotFoundException) {
            "1.0.0"
        }
    }

    fun String?.isYouTubeLink(): Boolean {
        return this?.contains("youtube.com") == true || this?.contains("youtu.be") == true
    }

    fun String?.getLinkFromYoutube(): String {
        val str = this ?: return ""
        return if (str.contains("v=")) {
            str.substringAfter("v=").substringBefore("&")
        } else if (str.contains("youtu.be/")) {
            str.substringAfter("youtu.be/")
        } else {
            str
        }
    }
}
