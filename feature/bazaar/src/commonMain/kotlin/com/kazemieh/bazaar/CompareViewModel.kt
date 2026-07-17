package com.kazemieh.bazaar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kazemieh.common.AppResult
import com.kazemieh.domain.marketplace.MarketplaceRepository
import com.kazemieh.domain.marketplace.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CompareViewModel(
    private val repository: MarketplaceRepository,
) : ViewModel() {
    private val _state = MutableStateFlow<AppResult<List<Product>>>(AppResult.Loading)
    val state = _state.asStateFlow()

    private var loaded = false

    fun load(productId: Long) {
        if (loaded) return
        loaded = true
        viewModelScope.launch {
            val product = repository.getProduct(productId)
            val others = repository.getOtherSellers(productId)
            val base = if (product is AppResult.Success) listOf(product.data) else emptyList()
            val rest = if (others is AppResult.Success) others.data else emptyList()
            _state.value = AppResult.Success((base + rest).sortedBy { it.price })
        }
    }
}
