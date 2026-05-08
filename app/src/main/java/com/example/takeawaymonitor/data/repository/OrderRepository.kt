package com.example.takeawaymonitor.data.repository

import com.example.takeawaymonitor.data.model.Order
import com.example.takeawaymonitor.data.model.OrderStatus
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    fun getAllOrders(): Flow<List<Order>>
    suspend fun getOrderById(id: String): Order?
    suspend fun addOrder(order: Order)
    suspend fun updateOrderStatus(orderId: String, status: OrderStatus)
    suspend fun deleteOrder(orderId: String)
    fun getOrdersByStatus(status: OrderStatus): Flow<List<Order>>
    suspend fun getTotalRevenue(): Double
    suspend fun getOrderCountByStatus(status: OrderStatus): Int
}
