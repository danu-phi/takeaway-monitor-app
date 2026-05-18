package com.example.takeawaymonitor.data.repository

import com.example.takeawaymonitor.data.model.Order
import com.example.takeawaymonitor.data.model.OrderStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

import javax.inject.Inject
import javax.inject.Singleton

/**
 * In-memory fake implementation of [OrderRepository] used for unit tests.
 */
@Singleton
class FakeOrderRepository @Inject constructor() : OrderRepository {

    private val _orders = MutableStateFlow<List<Order>>(emptyList())

    /** Seed the repository with a known list of orders. */
    fun setOrders(orders: List<Order>) {
        _orders.value = orders
    }

    override fun getAllOrders(): Flow<List<Order>> = _orders

    override suspend fun getOrderById(id: String): Order? =
        _orders.value.find { it.id == id }

    override suspend fun addOrder(order: Order) {
        _orders.value = _orders.value + order
    }

    override suspend fun updateOrderStatus(orderId: String, status: OrderStatus) {
        _orders.value = _orders.value.map { order ->
            if (order.id == orderId) order.copy(status = status) else order
        }
    }

    override suspend fun deleteOrder(orderId: String) {
        _orders.value = _orders.value.filter { it.id != orderId }
    }

    override fun getOrdersByStatus(status: OrderStatus): Flow<List<Order>> =
        _orders.map { orders -> orders.filter { it.status == status } }

    override suspend fun getTotalRevenue(): Double =
        _orders.value
            .filter { it.status == OrderStatus.DELIVERED }
            .sumOf { it.totalPrice }

    override suspend fun getOrderCountByStatus(status: OrderStatus): Int =
        _orders.value.count { it.status == status }
}
