package com.example.takeawaymonitor.data.repository

import com.example.takeawaymonitor.data.model.Order
import com.example.takeawaymonitor.data.model.OrderItem
import com.example.takeawaymonitor.data.model.OrderStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FakeOrderRepositoryTest {

    private lateinit var repository: FakeOrderRepository

    private fun order(id: String, status: OrderStatus, price: Double) = Order(
        id = id,
        customerName = "Customer $id",
        items = listOf(OrderItem("Item", 1, price)),
        status = status,
        totalPrice = price
    )

    @Before
    fun setUp() {
        repository = FakeOrderRepository()
    }

    @Test
    fun `getAllOrders emits empty list initially`() = runTest {
        val orders = repository.getAllOrders().first()
        assertTrue(orders.isEmpty())
    }

    @Test
    fun `addOrder appends order to list`() = runTest {
        val o = order("1", OrderStatus.PENDING, 10.0)
        repository.addOrder(o)
        val orders = repository.getAllOrders().first()
        assertEquals(1, orders.size)
        assertEquals(o, orders.first())
    }

    @Test
    fun `getOrderById returns correct order`() = runTest {
        repository.setOrders(listOf(order("A", OrderStatus.PENDING, 5.0)))
        val found = repository.getOrderById("A")
        assertNotNull(found)
        assertEquals("A", found!!.id)
    }

    @Test
    fun `getOrderById returns null for unknown id`() = runTest {
        val found = repository.getOrderById("UNKNOWN")
        assertNull(found)
    }

    @Test
    fun `updateOrderStatus changes status of matching order`() = runTest {
        repository.setOrders(listOf(order("1", OrderStatus.PENDING, 10.0)))
        repository.updateOrderStatus("1", OrderStatus.DELIVERED)
        val updated = repository.getOrderById("1")
        assertEquals(OrderStatus.DELIVERED, updated?.status)
    }

    @Test
    fun `deleteOrder removes order from list`() = runTest {
        repository.setOrders(listOf(order("1", OrderStatus.PENDING, 10.0)))
        repository.deleteOrder("1")
        val orders = repository.getAllOrders().first()
        assertTrue(orders.isEmpty())
    }

    @Test
    fun `getOrdersByStatus filters correctly`() = runTest {
        repository.setOrders(listOf(
            order("1", OrderStatus.PENDING, 10.0),
            order("2", OrderStatus.DELIVERED, 20.0),
            order("3", OrderStatus.PENDING, 15.0)
        ))
        val pending = repository.getOrdersByStatus(OrderStatus.PENDING).first()
        assertEquals(2, pending.size)
        assertTrue(pending.all { it.status == OrderStatus.PENDING })
    }

    @Test
    fun `getTotalRevenue sums only delivered orders`() = runTest {
        repository.setOrders(listOf(
            order("1", OrderStatus.DELIVERED, 100.0),
            order("2", OrderStatus.DELIVERED, 50.0),
            order("3", OrderStatus.PENDING, 200.0)  // must be excluded
        ))
        assertEquals(150.0, repository.getTotalRevenue(), 0.001)
    }

    @Test
    fun `getTotalRevenue is zero when no delivered orders`() = runTest {
        repository.setOrders(listOf(order("1", OrderStatus.PENDING, 100.0)))
        assertEquals(0.0, repository.getTotalRevenue(), 0.001)
    }

    @Test
    fun `getOrderCountByStatus returns correct count`() = runTest {
        repository.setOrders(listOf(
            order("1", OrderStatus.PENDING, 10.0),
            order("2", OrderStatus.PENDING, 20.0),
            order("3", OrderStatus.READY, 30.0)
        ))
        assertEquals(2, repository.getOrderCountByStatus(OrderStatus.PENDING))
        assertEquals(1, repository.getOrderCountByStatus(OrderStatus.READY))
        assertEquals(0, repository.getOrderCountByStatus(OrderStatus.CANCELLED))
    }
}
