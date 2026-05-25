package com.phsmk.id.takeaway_monitor.ui.viewmodel

import com.phsmk.id.takeaway_monitor.data.model.Order
import com.phsmk.id.takeaway_monitor.data.model.OrderItem
import com.phsmk.id.takeaway_monitor.data.model.OrderStatus
import com.phsmk.id.takeaway_monitor.data.repository.FakeOrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AnalyticsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeOrderRepository
    private lateinit var viewModel: AnalyticsViewModel

    private fun order(
        id: String,
        status: OrderStatus,
        price: Double,
        items: List<OrderItem> = listOf(OrderItem("Item", 1, price))
    ) = Order(id = id, customerName = "C$id", items = items, status = status, totalPrice = price)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeOrderRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── computeAnalytics (internal pure function) ─────────────────────────────────

    @Test
    fun `computeAnalytics - totalRevenue sums only delivered orders`() {
        repository.setOrders(emptyList())
        viewModel = AnalyticsViewModel(repository)
        val state = viewModel.computeAnalytics(
            listOf(
                order("1", OrderStatus.DELIVERED, 100.0),
                order("2", OrderStatus.DELIVERED,  50.0),
                order("3", OrderStatus.PENDING,   200.0)  // excluded
            )
        )
        assertEquals(150.0, state.totalRevenue, 0.001)
    }

    @Test
    fun `computeAnalytics - totalRevenue is zero with no delivered orders`() {
        repository.setOrders(emptyList())
        viewModel = AnalyticsViewModel(repository)
        val state = viewModel.computeAnalytics(listOf(order("1", OrderStatus.PENDING, 100.0)))
        assertEquals(0.0, state.totalRevenue, 0.001)
    }

    @Test
    fun `computeAnalytics - averageOrderValue is correct`() {
        repository.setOrders(emptyList())
        viewModel = AnalyticsViewModel(repository)
        val state = viewModel.computeAnalytics(
            listOf(
                order("1", OrderStatus.DELIVERED, 100.0),
                order("2", OrderStatus.DELIVERED,  60.0)
            )
        )
        assertEquals(80.0, state.averageOrderValue, 0.001)
    }

    @Test
    fun `computeAnalytics - averageOrderValue is zero when no delivered orders`() {
        repository.setOrders(emptyList())
        viewModel = AnalyticsViewModel(repository)
        val state = viewModel.computeAnalytics(listOf(order("1", OrderStatus.PENDING, 50.0)))
        assertEquals(0.0, state.averageOrderValue, 0.001)
    }

    @Test
    fun `computeAnalytics - totalOrdersDelivered is correct`() {
        repository.setOrders(emptyList())
        viewModel = AnalyticsViewModel(repository)
        val state = viewModel.computeAnalytics(
            listOf(
                order("1", OrderStatus.DELIVERED, 10.0),
                order("2", OrderStatus.DELIVERED, 20.0),
                order("3", OrderStatus.PENDING,   30.0)
            )
        )
        assertEquals(2, state.totalOrdersDelivered)
    }

    @Test
    fun `computeAnalytics - totalOrdersCancelled is correct`() {
        repository.setOrders(emptyList())
        viewModel = AnalyticsViewModel(repository)
        val state = viewModel.computeAnalytics(
            listOf(
                order("1", OrderStatus.CANCELLED, 10.0),
                order("2", OrderStatus.PENDING,   20.0)
            )
        )
        assertEquals(1, state.totalOrdersCancelled)
    }

    @Test
    fun `computeAnalytics - statusBreakdown contains all statuses`() {
        repository.setOrders(emptyList())
        viewModel = AnalyticsViewModel(repository)
        val state = viewModel.computeAnalytics(emptyList())
        assertEquals(OrderStatus.values().size, state.statusBreakdown.size)
    }

    @Test
    fun `computeAnalytics - statusBreakdown counts per status correctly`() {
        repository.setOrders(emptyList())
        viewModel = AnalyticsViewModel(repository)
        val state = viewModel.computeAnalytics(
            listOf(
                order("1", OrderStatus.PENDING,   10.0),
                order("2", OrderStatus.PENDING,   20.0),
                order("3", OrderStatus.DELIVERED, 30.0)
            )
        )
        assertEquals(2, state.statusBreakdown[OrderStatus.PENDING])
        assertEquals(1, state.statusBreakdown[OrderStatus.DELIVERED])
        assertEquals(0, state.statusBreakdown[OrderStatus.CANCELLED])
    }

    @Test
    fun `computeAnalytics - topItems returns at most 5 items sorted by quantity`() {
        repository.setOrders(emptyList())
        viewModel = AnalyticsViewModel(repository)
        val items = (1..7).map { i -> OrderItem("Item$i", i, 5.0) }
        val state = viewModel.computeAnalytics(
            listOf(order("1", OrderStatus.DELIVERED, 35.0, items))
        )
        assertTrue(state.topItems.size <= 5)
        // should be sorted descending by quantity
        val quantities = state.topItems.map { it.second }
        assertEquals(quantities.sortedDescending(), quantities)
    }

    @Test
    fun `computeAnalytics - topItems aggregates same item across multiple orders`() {
        repository.setOrders(emptyList())
        viewModel = AnalyticsViewModel(repository)
        val state = viewModel.computeAnalytics(
            listOf(
                order("1", OrderStatus.DELIVERED, 10.0, listOf(OrderItem("Burger", 3, 10.0))),
                order("2", OrderStatus.DELIVERED, 10.0, listOf(OrderItem("Burger", 2, 10.0)))
            )
        )
        val burgerEntry = state.topItems.find { it.first == "Burger" }
        assertNotNull(burgerEntry)
        assertEquals(5, burgerEntry!!.second)
    }

    // ── ViewModel state via Flow ──────────────────────────────────────────────────

    @Test
    fun `uiState totalRevenue is correct after loadAnalytics`() {
        repository.setOrders(listOf(
            order("1", OrderStatus.DELIVERED, 200.0),
            order("2", OrderStatus.PENDING,   100.0)
        ))
        viewModel = AnalyticsViewModel(repository)
        assertEquals(200.0, viewModel.uiState.value.totalRevenue, 0.001)
    }
}
