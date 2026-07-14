package com.kazemieh.bazaar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kazemieh.common.AppResult
import com.kazemieh.domain.marketplace.MarketplaceRepository
import com.kazemieh.domain.marketplace.Product
import com.kazemieh.domain.marketplace.Shop
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RastehSearchState(
    val tab: HomeTab = HomeTab.SHOPS,
    val shops: AppResult<List<Shop>> = AppResult.Loading,
    val products: AppResult<List<Product>> = AppResult.Loading,
)

class RastehSearchViewModel(
    private val repository: MarketplaceRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(RastehSearchState())
    val state = _state.asStateFlow()

    private var loaded = false

    fun load(locationId: Long, rastehId: Long?) {
        if (loaded) return
        loaded = true
        viewModelScope.launch {
            _state.update { it.copy(shops = repository.getShopsByLocation(locationId, rastehId)) }
        }
        viewModelScope.launch {
            _state.update { it.copy(products = repository.getProductsByLocation(locationId)) }
        }
    }

    fun onTab(tab: HomeTab) = _state.update { it.copy(tab = tab) }
}
