package com.kazemieh.bazaar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kazemieh.common.AppResult
import com.kazemieh.domain.advanced.AdvancedRepository
import com.kazemieh.domain.advanced.Escrow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EscrowState(
    val items: AppResult<List<Escrow>> = AppResult.Loading,
    val busy: Boolean = false,
    val message: String? = null,
)

/** پرداختِ امانی (escrow): بازکردنِ امانت + آزادسازی. */
class EscrowViewModel(
    private val repository: AdvancedRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(EscrowState())
    val state = _state.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch { _state.update { it.copy(items = repository.getEscrows()) } }
    }

    fun open(amount: Double, orderId: Long?) {
        if (_state.value.busy || amount <= 0) return
        _state.update { it.copy(busy = true) }
        viewModelScope.launch {
            when (repository.openEscrow(amount, orderId)) {
                is AppResult.Success -> { _state.update { it.copy(busy = false, message = "امانت باز شد") }; load() }
                else -> _state.update { it.copy(busy = false, message = "بازکردنِ امانت ناموفق بود") }
            }
        }
    }

    fun release(id: Long) {
        if (_state.value.busy) return
        _state.update { it.copy(busy = true) }
        viewModelScope.launch {
            when (repository.releaseEscrow(id)) {
                is AppResult.Success -> { _state.update { it.copy(busy = false, message = "مبلغ آزاد شد") }; load() }
                else -> _state.update { it.copy(busy = false, message = "آزادسازی ناموفق بود") }
            }
        }
    }

    fun consumeMessage() = _state.update { it.copy(message = null) }
}
