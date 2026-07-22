package com.kazemieh.bazaar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kazemieh.common.AppResult
import com.kazemieh.domain.advanced.AdvancedRepository
import com.kazemieh.domain.advanced.ParkingSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ParkingState(
    val items: AppResult<List<ParkingSession>> = AppResult.Loading,
    val busy: Boolean = false,
    val message: String? = null,
)

/** پارکینگِ من (parking): ثبتِ ورود، پرداخت، فهرست. */
class ParkingViewModel(
    private val repository: AdvancedRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(ParkingState())
    val state = _state.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch { _state.update { it.copy(items = repository.getParking()) } }
    }

    fun checkin(spot: String) {
        if (_state.value.busy || spot.isBlank()) return
        _state.update { it.copy(busy = true) }
        viewModelScope.launch {
            when (repository.checkinParking(spot.trim())) {
                is AppResult.Success -> { _state.update { it.copy(busy = false, message = "ورود ثبت شد") }; load() }
                else -> _state.update { it.copy(busy = false, message = "ثبتِ ورود ناموفق بود") }
            }
        }
    }

    fun pay(id: Long) {
        if (_state.value.busy) return
        _state.update { it.copy(busy = true) }
        viewModelScope.launch {
            when (repository.payParking(id)) {
                is AppResult.Success -> { _state.update { it.copy(busy = false, message = "پرداخت انجام شد") }; load() }
                else -> _state.update { it.copy(busy = false, message = "پرداخت ناموفق بود") }
            }
        }
    }

    fun consumeMessage() = _state.update { it.copy(message = null) }
}
