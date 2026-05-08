package com.example.takeawaymonitor.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.takeawaymonitor.data.model.Order
import com.example.takeawaymonitor.data.model.OrderStatus
import com.example.takeawaymonitor.data.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OrdersUiState(
    val orders: List<Order> = emptyList(),
    val filteredOrders: List<Order> = emptyList(),
    val selectedStatus: OrderStatus? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrdersUiState())
    val uiState: StateFlow<OrdersUiState> = _uiState.asStateFlow()

    init {
        observeOrders()
    }

    private fun observeOrders() {
        viewModelScope.launch {
            orderRepository.getAllOrders().collect { orders ->
                _uiState.update { state ->
                    val filtered = applyFilters(orders, state.selectedStatus, state.searchQuery)
                    state.copy(orders = orders, filteredOrders = filtered, isLoading = false)
                }
            }
        }
    }

    fun filterByStatus(status: OrderStatus?) {
        _uiState.update { state ->
            state.copy(
                selectedStatus = status,
                filteredOrders = applyFilters(state.orders, status, state.searchQuery)
            )
        }
    }

    fun searchOrders(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                filteredOrders = applyFilters(state.orders, state.selectedStatus, query)
            )
        }
    }

    fun updateOrderStatus(orderId: String, status: OrderStatus) {
        viewModelScope.launch {
            try {
                orderRepository.updateOrderStatus(orderId, status)
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message) }
            }
        }
    }

    private fun applyFilters(
        orders: List<Order>,
        status: OrderStatus?,
        query: String
    ): List<Order> = orders
        .filter { status == null || it.status == status }
        .filter { query.isBlank() || it.customerName.contains(query, ignoreCase = true) }
}
