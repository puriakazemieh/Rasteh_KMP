package com.kazemieh.bazaar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kazemieh.common.AppResult
import com.kazemieh.domain.marketplace.Product
import com.kazemieh.domain.marketplace.MarketplaceRepository
import com.kazemieh.domain.marketplace.Shop
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ShopDetailState(
    val shop: AppResult<Shop> = AppResult.Loading,
    val products: AppResult<List<Product>> = AppResult.Loading,
)

class ShopDetailViewModel(
    private val repository: MarketplaceRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ShopDetailState())
    val state = _state.asStateFlow()

    private var loaded = false

    fun load(shopId: Long) {
        if (loaded) return
        loaded = true
        viewModelScope.launch {
            _state.update { it.copy(shop = repository.getShop(shopId)) }
        }
        viewModelScope.launch {
            _state.update { it.copy(products = repository.getProductsByShop(shopId)) }
        }
    }
}
