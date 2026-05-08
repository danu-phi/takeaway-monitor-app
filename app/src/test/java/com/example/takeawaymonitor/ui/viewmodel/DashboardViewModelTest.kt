package com.example.takeawaymonitor.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.takeawaymonitor.data.model.Order
import com.example.takeawaymonitor.data.model.OrderItem
import com.example.takeawaymonitor.data.model.OrderStatus
import com.example.takeawaymonitor.data.repository.FakeOrderRepository
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
class DashboardViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeOrderRepository
    private lateinit var viewModel: DashboardViewModel

    private fun order(id: String, status: OrderStatus, price: Double) = Order(
        id = id,
        customerName = "Customer $id",
        items = listOf(OrderItem("Item", 1, price)),
        status = status,
        totalPrice = price
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeOrderRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has all zeros when repository is empty`() {
        viewModel = DashboardViewModel(repository)
        val state = viewModel.uiState.value
        assertEquals(0, state.totalOrders)
        assertEquals(0, state.pendingOrders)
        assertEquals(0, state.preparingOrders)
        assertEquals(0, state.readyOrders)
        assertEquals(0.0, state.totalRevenue, 0.001)
        assertFalse(state.isLoading)
        assertNull(state.errorMessage)
    }

    @Test
    fun `pendingOrders count is correct`() = runTest {
        repository.setOrders(listOf(
            order("1", OrderStatus.PENDING, 10.0),
            order("2", OrderStatus.PENDING, 20.0),
            order("3", OrderStatus.PREPARING, 15.0)
        ))
        viewModel = DashboardViewModel(repository)
        viewModel.loadDashboardData()
        assertEquals(2, viewModel.uiState.value.pendingOrders)
    }

    @Test
    fun `preparingOrders count is correct`() = runTest {
        repository.setOrders(listOf(
            order("1", OrderStatus.PREPARING, 10.0),
            order("2", OrderStatus.READY, 20.0)
        ))
        viewModel = DashboardViewModel(repository)
        viewModel.loadDashboardData()
        assertEquals(1, viewModel.uiState.value.preparingOrders)
    }

    @Test
    fun `readyOrders count is correct`() = runTest {
        repository.setOrders(listOf(
            order("1", OrderStatus.READY, 10.0),
            order("2", OrderStatus.READY, 20.0),
            order("3", OrderStatus.PENDING, 5.0)
        ))
        viewModel = DashboardViewModel(repository)
        viewModel.loadDashboardData()
        assertEquals(2, viewModel.uiState.value.readyOrders)
    }

    @Test
    fun `totalOrders is sum of pending + preparing + ready (excludes delivered and cancelled)`() = runTest {
        repository.setOrders(listOf(
            order("1", OrderStatus.PENDING,   10.0),
            order("2", OrderStatus.PREPARING, 15.0),
            order("3", OrderStatus.READY,     12.0),
            order("4", OrderStatus.DELIVERED, 20.0), // excluded
            order("5", OrderStatus.CANCELLED, 8.0)   // excluded
        ))
        viewModel = DashboardViewModel(repository)
        viewModel.loadDashboardData()
        assertEquals(3, viewModel.uiState.value.totalOrders)
    }

    @Test
    fun `totalRevenue sums only DELIVERED orders`() = runTest {
        repository.setOrders(listOf(
            order("1", OrderStatus.DELIVERED, 100.0),
            order("2", OrderStatus.DELIVERED, 50.0),
            order("3", OrderStatus.PENDING,   200.0) // must NOT be counted
        ))
        viewModel = DashboardViewModel(repository)
        viewModel.loadDashboardData()
        assertEquals(150.0, viewModel.uiState.value.totalRevenue, 0.001)
    }

    @Test
    fun `totalRevenue is zero when no delivered orders`() = runTest {
        repository.setOrders(listOf(order("1", OrderStatus.PENDING, 100.0)))
        viewModel = DashboardViewModel(repository)
        viewModel.loadDashboardData()
        assertEquals(0.0, viewModel.uiState.value.totalRevenue, 0.001)
    }

    @Test
    fun `isLoading is false after data loads successfully`() = runTest {
        viewModel = DashboardViewModel(repository)
        viewModel.loadDashboardData()
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `errorMessage is null after successful load`() = runTest {
        viewModel = DashboardViewModel(repository)
        viewModel.loadDashboardData()
        assertNull(viewModel.uiState.value.errorMessage)
    }
}
