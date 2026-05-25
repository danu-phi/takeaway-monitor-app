package com.phsmk.id.takeaway_monitor.data.repository

import com.phsmk.id.takeaway_monitor.data.model.Order
import com.phsmk.id.takeaway_monitor.data.model.OrderStatus
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
