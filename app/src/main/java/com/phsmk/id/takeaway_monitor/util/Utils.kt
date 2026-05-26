package com.phsmk.id.takeaway_monitor.util

import android.content.Context
import android.content.pm.PackageManager
import okhttp3.OkHttpClient
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object Utils {
    fun getUnsafeOkHttpClient(): OkHttpClient {
        return try {
            val trustAllCerts = arrayOf<TrustManager>(
                object : X509TrustManager {
                    override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
                    override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
                    override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
                }
            )
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            val sslSocketFactory = sslContext.socketFactory
            val builder = OkHttpClient.Builder()
            builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            builder.hostnameVerifier { _, _ -> true }
            builder.build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

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
