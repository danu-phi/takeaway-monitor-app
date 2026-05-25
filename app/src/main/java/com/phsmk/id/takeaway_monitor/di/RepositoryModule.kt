package com.phsmk.id.takeaway_monitor.di

import com.phsmk.id.takeaway_monitor.data.repository.FakeOrderRepository
import com.phsmk.id.takeaway_monitor.data.repository.OrderRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindOrderRepository(
        orderRepositoryImpl: FakeOrderRepository
    ): OrderRepository
}
