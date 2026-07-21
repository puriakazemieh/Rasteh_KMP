package com.kazemieh.bazaar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kazemieh.common.AppResult
import com.kazemieh.domain.advanced.AdvancedRepository
import com.kazemieh.domain.advanced.Warranty
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WarrantyState(
    val items: AppResult<List<Warranty>> = AppResult.Loading,
    val busy: Boolean = false,
    val message: String? = null,
)

/** دفترچهٔ ضمانتِ دیجیتال (warranty). */
class WarrantyViewModel(
    private val repository: AdvancedRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(WarrantyState())
    val state = _state.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch { _state.update { it.copy(items = repository.getWarranties()) } }
    }

    fun add(title: String, serial: String?) {
        if (_state.value.busy || title.isBlank()) return
        _state.update { it.copy(busy = true) }
        viewModelScope.launch {
            when (repository.createWarranty(title.trim(), serial?.trim()?.ifBlank { null })) {
                is AppResult.Success -> { _state.update { it.copy(busy = false, message = "ضمانت‌نامه ثبت شد") }; load() }
                else -> _state.update { it.copy(busy = false, message = "ثبتِ ضمانت‌نامه ناموفق بود") }
            }
        }
    }

    fun consumeMessage() = _state.update { it.copy(message = null) }
}
