package com.example.takeawaymonitor.data.model

import org.junit.Assert.*
import org.junit.Test

class OrderTest {

    private fun makeOrder(
        id: String = "1",
        customerName: String = "John",
        status: OrderStatus = OrderStatus.PENDING,
        items: List<OrderItem> = listOf(OrderItem("Burger", 2, 10.0), OrderItem("Fries", 1, 5.0)),
        totalPrice: Double = items.sumOf { it.price * it.quantity }
    ) = Order(id = id, customerName = customerName, items = items, status = status, totalPrice = totalPrice)

    // ── OrderItem tests ─────────────────────────────────────────────────────────

    @Test
    fun `orderItem price times quantity gives correct cost`() {
        val item = OrderItem("Pizza", 3, 12.5)
        assertEquals(37.5, item.price * item.quantity, 0.001)
    }

    @Test
    fun `orderItem with zero quantity has zero cost`() {
        val item = OrderItem("Salad", 0, 8.0)
        assertEquals(0.0, item.price * item.quantity, 0.001)
    }

    // ── Order tests ─────────────────────────────────────────────────────────────

    @Test
    fun `order total price matches sum of item costs`() {
        val items = listOf(OrderItem("Burger", 2, 10.0), OrderItem("Fries", 1, 5.0))
        val order = makeOrder(items = items, totalPrice = items.sumOf { it.price * it.quantity })
        assertEquals(25.0, order.totalPrice, 0.001)
    }

    @Test
    fun `order with empty items list has zero total price`() {
        val order = makeOrder(items = emptyList(), totalPrice = 0.0)
        assertEquals(0.0, order.totalPrice, 0.001)
    }

    @Test
    fun `copy changes only specified fields`() {
        val original = makeOrder(status = OrderStatus.PENDING)
        val updated  = original.copy(status = OrderStatus.DELIVERED)

        assertEquals(original.id, updated.id)
        assertEquals(original.customerName, updated.customerName)
        assertEquals(OrderStatus.DELIVERED, updated.status)
    }

    @Test
    fun `two orders with same id and fields are equal`() {
        val a = makeOrder(id = "X")
        val b = makeOrder(id = "X")
        assertEquals(a, b)
    }

    @Test
    fun `two orders with different id are not equal`() {
        val a = makeOrder(id = "1")
        val b = makeOrder(id = "2")
        assertNotEquals(a, b)
    }

    // ── OrderStatus tests ────────────────────────────────────────────────────────

    @Test
    fun `all expected statuses exist`() {
        val statuses = OrderStatus.values().toSet()
        assertTrue(OrderStatus.PENDING    in statuses)
        assertTrue(OrderStatus.PREPARING  in statuses)
        assertTrue(OrderStatus.READY      in statuses)
        assertTrue(OrderStatus.DELIVERED  in statuses)
        assertTrue(OrderStatus.CANCELLED  in statuses)
    }

    @Test
    fun `orderStatus valueOf returns correct enum`() {
        assertEquals(OrderStatus.PENDING,   OrderStatus.valueOf("PENDING"))
        assertEquals(OrderStatus.DELIVERED, OrderStatus.valueOf("DELIVERED"))
    }
}
