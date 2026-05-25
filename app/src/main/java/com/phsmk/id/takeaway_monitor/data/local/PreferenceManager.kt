package com.phsmk.id.takeaway_monitor.data.local

import android.content.Context
import android.content.SharedPreferences
import com.phsmk.id.takeaway_monitor.data.remote.model.ConfigData
import com.phsmk.id.takeaway_monitor.data.remote.model.PosConfigData
import com.phsmk.id.takeaway_monitor.data.remote.model.VersionData
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceManager @Inject constructor(
    @ApplicationContext context: Context,
    private val gson: Gson
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_CONFIG_DATA = "key_config_data"
        private const val KEY_POS_CONFIG_DATA = "key_pos_config_data"
        private const val KEY_APP_VERSION_DATA = "key_app_version_data"
    }

    fun saveConfig(config: ConfigData) {
        val json = gson.toJson(config)
        prefs.edit().putString(KEY_CONFIG_DATA, json).apply()
    }

    fun getConfig(): ConfigData? {
        val json = prefs.getString(KEY_CONFIG_DATA, null)
        return if (json != null) {
            gson.fromJson(json, ConfigData::class.java)
        } else {
            null
        }
    }

    fun savePosConfig(config: PosConfigData) {
        val json = gson.toJson(config)
        prefs.edit().putString(KEY_POS_CONFIG_DATA, json).apply()
    }

    fun getPosConfig(): PosConfigData? {
        val json = prefs.getString(KEY_POS_CONFIG_DATA, null)
        return if (json != null) {
            gson.fromJson(json, PosConfigData::class.java)
        } else {
            null
        }
    }

    fun saveAppVersion(version: VersionData) {
        val json = gson.toJson(version)
        prefs.edit().putString(KEY_APP_VERSION_DATA, json).apply()
    }

    fun getAppVersion(): VersionData? {
        val json = prefs.getString(KEY_APP_VERSION_DATA, null)
        return if (json != null) {
            gson.fromJson(json, VersionData::class.java)
        } else {
            null
        }
    }
}
