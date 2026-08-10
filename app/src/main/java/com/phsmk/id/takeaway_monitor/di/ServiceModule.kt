package com.phsmk.id.takeaway_monitor.di

import android.content.Context
import com.phsmk.id.takeaway_monitor.util.NsdHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {

    @Provides
    @Singleton
    fun provideNsdHelper(@ApplicationContext context: Context): NsdHelper {
        return NsdHelper(context)
    }
}
