package com.example.takeawaymonitor.di

import com.example.takeawaymonitor.data.repository.FakeOrderRepository
import com.example.takeawaymonitor.data.repository.OrderRepository
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
