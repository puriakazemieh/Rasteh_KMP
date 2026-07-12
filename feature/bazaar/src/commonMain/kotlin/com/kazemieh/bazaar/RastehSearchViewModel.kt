package com.kazemieh.bazaar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kazemieh.common.AppResult
import com.kazemieh.domain.marketplace.MarketplaceRepository
import com.kazemieh.domain.marketplace.Shop
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RastehSearchViewModel(
    private val repository: MarketplaceRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<AppResult<List<Shop>>>(AppResult.Loading)
    val state = _state.asStateFlow()

    private var loaded = false

    fun load(locationId: Long, rastehId: Long?) {
        if (loaded) return
        loaded = true
        viewModelScope.launch {
            _state.value = AppResult.Loading
            _state.value = repository.getShopsByLocation(locationId, rastehId)
        }
    }
}
