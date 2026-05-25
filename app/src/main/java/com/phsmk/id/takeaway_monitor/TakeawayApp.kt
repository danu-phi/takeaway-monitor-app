package com.phsmk.id.takeaway_monitor

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TakeawayApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}