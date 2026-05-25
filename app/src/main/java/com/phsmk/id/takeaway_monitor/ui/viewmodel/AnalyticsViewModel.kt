package com.phsmk.id.takeaway_monitor.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phsmk.id.takeaway_monitor.data.model.Order
import com.phsmk.id.takeaway_monitor.data.model.OrderStatus
import com.phsmk.id.takeaway_monitor.data.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AnalyticsUiState(
    val totalRevenue: Double = 0.0,
    val totalOrdersDelivered: Int = 0,
    val totalOrdersCancelled: Int = 0,
    val averageOrderValue: Double = 0.0,
    val statusBreakdown: Map<OrderStatus, Int> = emptyMap(),
    val topItems: List<Pair<String, Int>> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    init {
        loadAnalytics()
    }

    fun loadAnalytics() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            orderRepository.getAllOrders().collect { orders ->
                _uiState.value = computeAnalytics(orders)
            }
        }
    }

    internal fun computeAnalytics(orders: List<Order>): AnalyticsUiState {
        val delivered    = orders.filter { it.status == OrderStatus.DELIVERED }
        val cancelled    = orders.filter { it.status == OrderStatus.CANCELLED }
        val totalRevenue = delivered.sumOf { it.totalPrice }
        val avgValue     = if (delivered.isNotEmpty()) totalRevenue / delivered.size else 0.0

        val statusBreakdown = OrderStatus.values().associateWith { s ->
            orders.count { it.status == s }
        }

        val topItems = orders
            .flatMap { o -> o.items.map { it.name to it.quantity } }
            .groupBy { it.first }
            .mapValues { (_, v) -> v.sumOf { it.second } }
            .toList()
            .sortedByDescending { it.second }
            .take(5)

        return AnalyticsUiState(
            totalRevenue        = totalRevenue,
            totalOrdersDelivered = delivered.size,
            totalOrdersCancelled = cancelled.size,
            averageOrderValue   = avgValue,
            statusBreakdown     = statusBreakdown,
            topItems            = topItems
        )
    }
}
