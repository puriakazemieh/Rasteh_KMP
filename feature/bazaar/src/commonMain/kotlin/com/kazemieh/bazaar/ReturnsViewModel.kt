package com.kazemieh.bazaar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kazemieh.common.AppResult
import com.kazemieh.domain.services.ReturnRequest
import com.kazemieh.domain.services.ServicesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ReturnsState(
    val items: AppResult<List<ReturnRequest>> = AppResult.Loading,
    val busy: Boolean = false,
    val message: String? = null,
)

/** مرکزِ بازگشتِ کالا (returns): فهرستِ درخواست‌ها + ثبتِ درخواستِ جدید. */
class ReturnsViewModel(
    private val repository: ServicesRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(ReturnsState())
    val state = _state.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch { _state.update { it.copy(items = repository.getReturns()) } }
    }

    fun submit(orderId: Long, reason: String?) {
        if (_state.value.busy) return
        _state.update { it.copy(busy = true) }
        viewModelScope.launch {
            when (repository.createReturn(orderId, reason?.trim()?.ifBlank { null })) {
                is AppResult.Success -> { _state.update { it.copy(busy = false, message = "درخواستِ مرجوعی ثبت شد") }; load() }
                else -> _state.update { it.copy(busy = false, message = "ثبتِ درخواست ناموفق بود") }
            }
        }
    }

    fun consumeMessage() = _state.update { it.copy(message = null) }
}
