package com.phsmk.id.takeaway_monitor.ui.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phsmk.id.takeaway_monitor.data.local.PreferenceManager
import com.phsmk.id.takeaway_monitor.data.remote.ApiService
import com.phsmk.id.takeaway_monitor.data.remote.model.ConfigResponse
import com.phsmk.id.takeaway_monitor.data.remote.model.PosConfigResponse
import com.phsmk.id.takeaway_monitor.data.remote.model.AppVersionResponse
import com.phsmk.id.takeaway_monitor.data.remote.model.VersionData
import com.phsmk.id.takeaway_monitor.util.Constants
import com.phsmk.id.takeaway_monitor.util.StringUtil
import com.phsmk.id.takeaway_monitor.util.Utils
import com.phsmk.id.takeaway_monitor.util.NsdHelper
import com.phsmk.id.takeaway_monitor.data.remote.WebSocketManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.text.DecimalFormat
import javax.inject.Inject
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

@HiltViewModel
class MainViewModel @Inject constructor(
    private val apiService: ApiService,
    private val preferenceManager: PreferenceManager,
    private val nsdHelper: NsdHelper,
    private val webSocketManager: WebSocketManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _configState = MutableStateFlow<ConfigResponse?>(null)
    val configState: StateFlow<ConfigResponse?> = _configState

    private val _posConfigState = MutableStateFlow<PosConfigResponse?>(null)
    val posConfigState: StateFlow<PosConfigResponse?> = _posConfigState

    private val _appVersionState = MutableStateFlow<AppVersionResponse?>(null)
    val appVersionState: StateFlow<AppVersionResponse?> = _appVersionState

    private val _isUpdateAvailable = MutableStateFlow(false)
    val isUpdateAvailable: StateFlow<Boolean> = _isUpdateAvailable

    private val _showUpdateDialog = MutableStateFlow<Pair<VersionData, String>?>(null)
    val showUpdateDialog: StateFlow<Pair<VersionData, String>?> = _showUpdateDialog

    private val _downloadProgress = MutableStateFlow<Int?>(null)
    val downloadProgress: StateFlow<Int?> = _downloadProgress

    private val _navigateToOrders = MutableSharedFlow<Unit>()
    val navigateToOrders: SharedFlow<Unit> = _navigateToOrders.asSharedFlow()

    private val _startPushService = MutableSharedFlow<Unit>()
    val startPushService: SharedFlow<Unit> = _startPushService.asSharedFlow()

    var urlDownload: String? = null
        private set

    var fileSize: String = "0"
        private set

    private val _isFetching = MutableStateFlow(false)
    val isFetching: StateFlow<Boolean> = _isFetching

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState

    private var configTimeoutJob: Job? = null

    init {
        viewModelScope.launch {
            webSocketManager.onConfigReceived.collect { response ->
                configTimeoutJob?.cancel()
                _posConfigState.value = response
                preferenceManager.savePosConfig(response.data)
                println("WebSocket: POS Config received and saved successfully")

                fetchAppVersion()
                _startPushService.emit(Unit)
                _isFetching.value = false
            }
        }
    }

    fun fetchConfigs() {
        if (_isFetching.value) return
        _isFetching.value = true
//        _errorState.value = null

        configTimeoutJob?.cancel()
        configTimeoutJob = viewModelScope.launch {
            startFindingServer()
            delay(15000) // 15 seconds timeout
            if (_isFetching.value) {
                println("Config timeout: Falling back to REST API")
                fetchConfigsRest()
            }
        }
    }

    private fun fetchConfigsRest() {
        viewModelScope.launch {
            try {
                // Fetch second config (POS Config)
                val response = apiService.getPosConfig()
                _posConfigState.value = response
                preferenceManager.savePosConfig(response.data)
                println("REST: POS Config loaded and saved successfully")

                // If success, fetch app version
                fetchAppVersion()
                _startPushService.emit(Unit)
            } catch (e: Exception) {
                e.printStackTrace()
                _errorState.value = "Failed to load configs: ${e.message}"
            } finally {
                _isFetching.value = false
            }
        }
    }

    private suspend fun fetchAppVersion() {
        try {
            val response = apiService.getAppVersion()
            _appVersionState.value = response
            response.data?.let {
                preferenceManager.saveAppVersion(it)
            }
            println("App Version loaded and saved successfully")
            
            // Added check version logic
            checkNewVersion(response)
        } catch (e: Exception) {
            e.printStackTrace()
            _errorState.value = "Failed to load app version: ${e.message}"
            println("Failed to load app version: ${e.message}")
        }
    }

    fun dismissError() {
        _errorState.value = null
    }

    fun startFindingServer() {
        nsdHelper.discoverServices("_master-service._tcp.") { serviceInfo ->
            // Logic when server is found via NSD
            val host = serviceInfo.host.hostAddress
            val port = serviceInfo.port
            println("NSD: Server found at $host:8000")
            
            // Connect to WebSocket using discovered host
            webSocketManager.connect("http://$host:8000")
        }
    }

    fun stopFindingServer() {
        nsdHelper.stopDiscovery()
        webSocketManager.disconnect()
    }

    override fun onCleared() {
        super.onCleared()
        stopFindingServer()
    }

    fun checkNewVersion(versionResponse: AppVersionResponse?): Boolean {
        if (versionResponse == null) return false
        try {
            val version = versionResponse.data
            if (version != null && StringUtil.checkLastVersion(
                    Utils.getVersion(context),
                    version.version
                ) < 0
            ) {
                urlDownload = version.path_file
                getFileSize(version)
                _isUpdateAvailable.value = true
                return true
            } else {
                _isUpdateAvailable.value = false
                val outletLogo = preferenceManager.getPosConfig()?.outletLogo
                downloadlogo(outletLogo) {
                    viewModelScope.launch {
                        _navigateToOrders.emit(Unit)
                    }
                }
                return false
            }
        } catch (ex: Exception) {
            Toast.makeText(context, "There're some errors when check app version!", Toast.LENGTH_SHORT).show()
            println("There're some errors when check app version!: ${ex.message}")
            return false
        }
    }

    private fun downloadlogo(outletLogo: String?, onComplete: () -> Unit = {}) {
        if (outletLogo.isNullOrEmpty()) {
            onComplete()
            return
        }
        val fallbackUrl = if (outletLogo.contains("https://ph_posmanager")) {
            outletLogo.replace("https://ph_posmanager", "https://ph-posmanager")
        } else
            outletLogo
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.downloadFile(fallbackUrl)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        val folderPath = "${context.filesDir.absolutePath}/${Constants.LOGO_FOLDER}"
                        val fileName = Utils.getFileNameFromUrl(fallbackUrl)
                        Utils.saveInputStreamToFile(body.byteStream(), folderPath, fileName)
                        println("Logo downloaded and saved successfully to $folderPath/$fileName")
                    }
                } else {
                    println("Failed to download logo: ${response.message()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                println("Error downloading logo: ${e.message}")
            } finally {
                withContext(Dispatchers.Main) {
                    onComplete()
                }
            }
        }
    }

    private fun getFileSize(version: VersionData) {
        val downloadUrl = urlDownload ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val size = calculateFileSize(downloadUrl)
                withContext(Dispatchers.Main) {
                    fileSize = size
                    _showUpdateDialog.value = version to fileSize
                }
            } catch (e: Exception) {
                try {
                    val fallbackUrl = if (downloadUrl.contains("https://ph-posmanager")) {
                        downloadUrl.replace("https://ph_posmanager", "https://ph-posmanager")
                    } else
                        downloadUrl
                    val size = calculateFileSize(fallbackUrl)
                    withContext(Dispatchers.Main) {
                        fileSize = size
                        _showUpdateDialog.value = version to fileSize
                    }
                } catch (e2: Exception) {
                    withContext(Dispatchers.Main) {
                        fileSize = "0"
                        _showUpdateDialog.value = version to fileSize
                    }
                }
            }
        }
    }

    private fun calculateFileSize(urlString: String): String {
        var urlConnection: java.net.HttpURLConnection? = null
        try {
            val url = URL(urlString)
            urlConnection = url.openConnection() as java.net.HttpURLConnection
            
            if (urlConnection is HttpsURLConnection) {
                val sslContext = SSLContext.getInstance("TLS")
                sslContext.init(
                    null,
                    arrayOf<X509TrustManager>(object : X509TrustManager {
                        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
                        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
                        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
                    }),
                    SecureRandom()
                )
                urlConnection.sslSocketFactory = sslContext.socketFactory
            }
            
            urlConnection.requestMethod = "GET"
            urlConnection.connect()
            val length = urlConnection.contentLength
            val decimalFormat = DecimalFormat("#.##")
            return decimalFormat.format(length.toDouble() / 1024 / 1024)
        } finally {
            urlConnection?.disconnect()
        }
    }

    fun onUpdateDialogDismissed() {
        _showUpdateDialog.value = null
    }

    fun startDownload(onDownloadComplete: (java.io.File) -> Unit) {
        val downloadUrl = urlDownload ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                withContext(Dispatchers.Main) { _downloadProgress.value = 0 }
                
                val url = URL(downloadUrl)
                val filename = downloadUrl.substringAfterLast("/").replace("%20", "_")
                
                val sslContext = SSLContext.getInstance("TLS")
                sslContext.init(null, arrayOf<X509TrustManager>(object : X509TrustManager {
                    override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
                    override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
                    override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
                }), SecureRandom())
                
                val connection = url.openConnection() as java.net.HttpURLConnection
                if (connection is HttpsURLConnection) {
                    val sslContext = SSLContext.getInstance("TLS")
                    sslContext.init(null, arrayOf<X509TrustManager>(object : X509TrustManager {
                        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
                        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
                        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
                    }), SecureRandom())
                    connection.sslSocketFactory = sslContext.socketFactory
                }
                connection.connect()

                val path = context.cacheDir
                val outputFile = java.io.File(path, filename)
                if (outputFile.exists()) outputFile.delete()

                val inputStream = connection.inputStream
                val outputStream = java.io.FileOutputStream(outputFile)
                val totalSize = connection.contentLength
                val buffer = ByteArray(1024)
                var downloaded = 0L
                var bytesRead: Int

                try {
                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                        downloaded += bytesRead
                        if (totalSize > 0) {
                            val progress = (downloaded * 100 / totalSize).toInt()
                            withContext(Dispatchers.Main) { _downloadProgress.value = progress }
                        }
                    }
                } finally {
                    outputStream.close()
                    inputStream.close()
                    connection.disconnect()
                }

                withContext(Dispatchers.Main) {
                    _downloadProgress.value = null
                    onDownloadComplete(outputFile)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _downloadProgress.value = null
                    Toast.makeText(context, "Download Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
