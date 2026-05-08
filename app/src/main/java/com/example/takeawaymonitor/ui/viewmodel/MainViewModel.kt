package com.example.takeawaymonitor.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.takeawaymonitor.data.local.PreferenceManager
import com.example.takeawaymonitor.data.remote.ApiService
import com.example.takeawaymonitor.data.remote.model.ConfigResponse
import com.example.takeawaymonitor.data.remote.model.PosConfigResponse
import com.example.takeawaymonitor.data.remote.model.AppVersionResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val apiService: ApiService,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    private val _configState = MutableStateFlow<ConfigResponse?>(null)
    val configState: StateFlow<ConfigResponse?> = _configState

    private val _posConfigState = MutableStateFlow<PosConfigResponse?>(null)
    val posConfigState: StateFlow<PosConfigResponse?> = _posConfigState

    private val _appVersionState = MutableStateFlow<AppVersionResponse?>(null)
    val appVersionState: StateFlow<AppVersionResponse?> = _appVersionState

    fun fetchConfigs() {
        viewModelScope.launch {
            // Fetch first config
            try {
                val response = apiService.getConfig()
                _configState.value = response
                preferenceManager.saveConfig(response.data)
                println("Config loaded and saved successfully")
            } catch (e: Exception) {
                e.printStackTrace()
                println("Failed to load first config: ${e.message}")
            }

            // Fetch second config (POS Config)
            try {
                val response = apiService.getPosConfig()
                _posConfigState.value = response
                preferenceManager.savePosConfig(response.data)
                println("POS Config loaded and saved successfully")

                // If success, fetch app version
                fetchAppVersion()
            } catch (e: Exception) {
                e.printStackTrace()
                println("Failed to load POS config: ${e.message}")
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
        } catch (e: Exception) {
            e.printStackTrace()
            println("Failed to load app version: ${e.message}")
        }
    }
}
