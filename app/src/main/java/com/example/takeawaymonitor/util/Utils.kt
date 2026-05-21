package com.example.takeawaymonitor.util

import android.content.Context
import android.content.pm.PackageManager
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

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

    fun saveInputStreamToFile(inputStream: InputStream, folderPath: String, fileName: String) {
        val folder = File(folderPath)
        if (!folder.exists()) {
            folder.mkdirs()
        }
        val file = File(folder, fileName)
        FileOutputStream(file).use { output ->
            inputStream.copyTo(output)
        }
    }

    fun getFileNameFromUrl(url: String): String {
        return url.substringAfterLast("/")
    }
}
