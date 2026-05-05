package com.example.takeawaymonitor

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TakeawayApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}