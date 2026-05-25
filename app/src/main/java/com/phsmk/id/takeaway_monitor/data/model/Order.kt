package com.phsmk.id.takeaway_monitor.data.model

import java.util.Date

data class Order(
    val id: String,
    val customerName: String,
    val items: List<OrderItem>,
    val status: OrderStatus,
    val totalPrice: Double,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

data class OrderItem(
    val name: String,
    val quantity: Int,
    val price: Double
)

enum class OrderStatus {
    PENDING,
    PREPARING,
    READY,
    DELIVERED,
    CANCELLED
}
