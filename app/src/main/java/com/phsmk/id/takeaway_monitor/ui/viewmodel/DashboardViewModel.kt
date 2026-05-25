package com.phsmk.id.takeaway_monitor.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phsmk.id.takeaway_monitor.data.model.OrderStatus
import com.phsmk.id.takeaway_monitor.data.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val totalOrders: Int = 0,
    val pendingOrders: Int = 0,
    val preparingOrders: Int = 0,
    val readyOrders: Int = 0,
    val totalRevenue: Double = 0.0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val pendingCount   = orderRepository.getOrderCountByStatus(OrderStatus.PENDING)
                val preparingCount = orderRepository.getOrderCountByStatus(OrderStatus.PREPARING)
                val readyCount     = orderRepository.getOrderCountByStatus(OrderStatus.READY)
                val revenue        = orderRepository.getTotalRevenue()

                _uiState.update {
                    it.copy(
                        totalOrders    = pendingCount + preparingCount + readyCount,
                        pendingOrders  = pendingCount,
                        preparingOrders = preparingCount,
                        readyOrders    = readyCount,
                        totalRevenue   = revenue,
                        isLoading      = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
}
