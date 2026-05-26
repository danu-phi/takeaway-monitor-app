package com.phsmk.id.takeaway_monitor

import android.app.Application
import com.phsmk.id.takeaway_monitor.data.DataManager
import com.phsmk.id.takeaway_monitor.data.local.PreferenceManager
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class TakeawayApp : Application() {

    @Inject
    lateinit var dataManager: DataManager

    @Inject
    lateinit var preferenceManager: PreferenceManager

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}