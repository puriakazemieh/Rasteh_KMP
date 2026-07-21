package com.kazemieh.bazaar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kazemieh.common.AppResult
import com.kazemieh.domain.services.Appointment
import com.kazemieh.domain.services.ServicesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AppointmentsState(
    val items: AppResult<List<Appointment>> = AppResult.Loading,
)

/** «نوبت‌های من» — رزروهای بازدیدِ حضوریِ کاربر. */
class AppointmentsViewModel(
    private val repository: ServicesRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(AppointmentsState())
    val state = _state.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch { _state.update { it.copy(items = repository.getAppointments()) } }
    }
}
