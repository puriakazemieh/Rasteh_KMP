package com.kazemieh.bazaar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kazemieh.common.AppResult
import com.kazemieh.domain.features.FeaturesRepository
import com.kazemieh.domain.features.PriceAlert
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PriceAlertsViewModel(
    private val repository: FeaturesRepository,
) : ViewModel() {
    private val _state = MutableStateFlow<AppResult<List<PriceAlert>>>(AppResult.Loading)
    val state = _state.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch { _state.value = repository.getPriceAlerts() }
    }

    fun remove(id: Long) {
        viewModelScope.launch {
            repository.deletePriceAlert(id)
            load()
        }
    }
}
