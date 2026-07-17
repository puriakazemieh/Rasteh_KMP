package com.kazemieh.bazaar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kazemieh.common.AppResult
import com.kazemieh.domain.marketplace.City
import com.kazemieh.domain.marketplace.MarketplaceRepository
import com.kazemieh.domain.marketplace.Rasteh
import com.kazemieh.domain.marketplace.Report
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminManageState(
    val tab: Int = 0,   // 0 راسته/محل · 1 گزارش‌ها
    val rastehs: AppResult<List<Rasteh>> = AppResult.Loading,
    val cities: List<City> = emptyList(),
    val reports: AppResult<List<Report>> = AppResult.Loading,
    val busy: Boolean = false,
    val message: String? = null,
)

class AdminManageViewModel(
    private val repository: MarketplaceRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(AdminManageState())
    val state = _state.asStateFlow()

    init { loadRefs(); loadReports() }

    fun onTab(tab: Int) = _state.update { it.copy(tab = tab) }

    private fun loadRefs() {
        viewModelScope.launch { _state.update { it.copy(rastehs = repository.getRastehs()) } }
        viewModelScope.launch {
            when (val c = repository.getCities(null)) {
                is AppResult.Success -> _state.update { it.copy(cities = c.data) }
                else -> {}
            }
        }
    }

    private fun loadReports() {
        viewModelScope.launch { _state.update { it.copy(reports = repository.getAdminReports(null)) } }
    }

    fun createRasteh(label: String) {
        if (label.isBlank() || _state.value.busy) return
        _state.update { it.copy(busy = true) }
        viewModelScope.launch {
            when (repository.adminCreateRasteh(label.trim(), null, null, 0)) {
                is AppResult.Success -> { _state.update { it.copy(busy = false, message = "راسته اضافه شد") }; _state.update { it.copy(rastehs = repository.getRastehs()) } }
                else -> _state.update { it.copy(busy = false, message = "افزودنِ راسته ناموفق بود") }
            }
        }
    }

    fun createLocation(cityId: Long, name: String, kind: String) {
        if (name.isBlank() || _state.value.busy) return
        _state.update { it.copy(busy = true) }
        viewModelScope.launch {
            when (repository.adminCreateLocation(cityId, name.trim(), kind, null, 1)) {
                is AppResult.Success -> _state.update { it.copy(busy = false, message = "محل اضافه شد") }
                else -> _state.update { it.copy(busy = false, message = "افزودنِ محل ناموفق بود") }
            }
        }
    }

    fun resolveReport(id: Long, status: String) {
        viewModelScope.launch {
            repository.resolveReport(id, status)
            loadReports()
        }
    }

    fun consumeMessage() = _state.update { it.copy(message = null) }
}
