package com.phsmk.id.takeaway_monitor.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.phsmk.id.takeaway_monitor.data.model.Order
import com.phsmk.id.takeaway_monitor.data.model.OrderItem
import com.phsmk.id.takeaway_monitor.data.model.OrderStatus
import com.phsmk.id.takeaway_monitor.data.repository.FakeOrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OrdersViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeOrderRepository
    private lateinit var viewModel: OrdersViewModel

    private val sampleOrders = listOf(
        Order("1", "Alice",   listOf(OrderItem("Burger", 1, 10.0)), OrderStatus.PENDING,   10.0),
        Order("2", "Bob",     listOf(OrderItem("Pizza",  2, 15.0)), OrderStatus.PREPARING, 30.0),
        Order("3", "Charlie", listOf(OrderItem("Sushi",  1, 20.0)), OrderStatus.READY,     20.0),
        Order("4", "Alice",   listOf(OrderItem("Fries",  3,  5.0)), OrderStatus.DELIVERED, 15.0)
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeOrderRepository()
        repository.setOrders(sampleOrders)
        viewModel = OrdersViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── Initial state ────────────────────────────────────────────────────────────

    @Test
    fun `all orders are loaded on init`() {
        assertEquals(sampleOrders.size, viewModel.uiState.value.orders.size)
    }

    @Test
    fun `filtered orders equals all orders when no filter applied`() {
        assertEquals(sampleOrders.size, viewModel.uiState.value.filteredOrders.size)
    }

    // ── filterByStatus ───────────────────────────────────────────────────────────

    @Test
    fun `filterByStatus returns only matching orders`() {
        viewModel.filterByStatus(OrderStatus.PENDING)
        val filtered = viewModel.uiState.value.filteredOrders
        assertEquals(1, filtered.size)
        assertTrue(filtered.all { it.status == OrderStatus.PENDING })
    }

    @Test
    fun `filterByStatus null clears filter and returns all orders`() {
        viewModel.filterByStatus(OrderStatus.PENDING)
        viewModel.filterByStatus(null)
        assertEquals(sampleOrders.size, viewModel.uiState.value.filteredOrders.size)
    }

    @Test
    fun `filterByStatus updates selectedStatus in ui state`() {
        viewModel.filterByStatus(OrderStatus.READY)
        assertEquals(OrderStatus.READY, viewModel.uiState.value.selectedStatus)
    }

    @Test
    fun `filterByStatus with no matching orders returns empty list`() {
        viewModel.filterByStatus(OrderStatus.CANCELLED)
        assertTrue(viewModel.uiState.value.filteredOrders.isEmpty())
    }

    // ── searchOrders ─────────────────────────────────────────────────────────────

    @Test
    fun `searchOrders filters by name case-insensitively`() {
        viewModel.searchOrders("alice")
        val filtered = viewModel.uiState.value.filteredOrders
        assertEquals(2, filtered.size)
        assertTrue(filtered.all { it.customerName.equals("Alice", ignoreCase = true) })
    }

    @Test
    fun `searchOrders with blank query returns all orders`() {
        viewModel.searchOrders("xyz")
        viewModel.searchOrders("")
        assertEquals(sampleOrders.size, viewModel.uiState.value.filteredOrders.size)
    }

    @Test
    fun `searchOrders with no match returns empty list`() {
        viewModel.searchOrders("ZZZNOMATCH")
        assertTrue(viewModel.uiState.value.filteredOrders.isEmpty())
    }

    @Test
    fun `searchQuery is saved in ui state`() {
        viewModel.searchOrders("Bob")
        assertEquals("Bob", viewModel.uiState.value.searchQuery)
    }

    // ── Combined filter + search ─────────────────────────────────────────────────

    @Test
    fun `filter by status and search by name can be combined`() {
        viewModel.filterByStatus(OrderStatus.DELIVERED)
        viewModel.searchOrders("alice")
        val filtered = viewModel.uiState.value.filteredOrders
        assertEquals(1, filtered.size)
        assertEquals("Alice", filtered.first().customerName)
        assertEquals(OrderStatus.DELIVERED, filtered.first().status)
    }

    @Test
    fun `combined filter with no match returns empty list`() {
        viewModel.filterByStatus(OrderStatus.CANCELLED)
        viewModel.searchOrders("Alice")
        assertTrue(viewModel.uiState.value.filteredOrders.isEmpty())
    }

    // ── updateOrderStatus ────────────────────────────────────────────────────────

    @Test
    fun `updateOrderStatus changes status in repository`() = runTest {
        viewModel.updateOrderStatus("1", OrderStatus.PREPARING)
        val updated = repository.getOrderById("1")
        assertEquals(OrderStatus.PREPARING, updated?.status)
    }

    @Test
    fun `updateOrderStatus for non-existent id does not crash`() = runTest {
        viewModel.updateOrderStatus("NONEXISTENT", OrderStatus.READY)
        // no exception expected; state should be clean
        assertNull(viewModel.uiState.value.errorMessage)
    }
}
