package com.kazemieh.bazaar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kazemieh.common.AppResult
import com.kazemieh.domain.interaction.InteractionRepository
import com.kazemieh.domain.marketplace.MarketplaceRepository
import com.kazemieh.domain.marketplace.Product
import com.kazemieh.domain.marketplace.RecentlyViewedMarketRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProductDetailState(
    val product: AppResult<Product> = AppResult.Loading,
    val otherSellers: List<Product> = emptyList(),
    val busy: Boolean = false,
    val message: String? = null,
)

sealed interface ProductDetailEffect {
    data class OpenChat(val conversationId: Long, val title: String) : ProductDetailEffect
}

class ProductDetailViewModel(
    private val repository: MarketplaceRepository,
    private val interaction: InteractionRepository,
    private val recentlyViewed: RecentlyViewedMarketRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ProductDetailState())
    val state = _state.asStateFlow()

    private val _effect = Channel<ProductDetailEffect>()
    val effect = _effect.receiveAsFlow()

    private var loaded = false
    private var shopId: Long = 0

    fun load(productId: Long) {
        if (loaded) return
        loaded = true
        viewModelScope.launch {
            val res = repository.getProduct(productId)
            if (res is AppResult.Success) {
                shopId = res.data.shopId ?: 0
                recentlyViewed.add(res.data)
            }
            _state.update { it.copy(product = res) }
        }
        viewModelScope.launch {
            when (val res = repository.getOtherSellers(productId)) {
                is AppResult.Success -> _state.update { it.copy(otherSellers = res.data) }
                else -> {}
            }
        }
    }

    fun buy(product: Product) {
        if (_state.value.busy || product.shopId == null) return
        _state.update { it.copy(busy = true) }
        viewModelScope.launch {
            when (repository.createOrder(product.shopId!!, listOf(product.id to 1), null)) {
                is AppResult.Success -> _state.update { it.copy(busy = false, message = "سفارشِ شما ثبت شد") }
                else -> _state.update { it.copy(busy = false, message = "ثبتِ سفارش ناموفق بود") }
            }
        }
    }

    fun submitOffer(amount: Double, message: String?, productId: Long) {
        if (shopId <= 0) return
        _state.update { it.copy(busy = true) }
        viewModelScope.launch {
            when (interaction.createOffer(shopId, productId, amount, message)) {
                is AppResult.Success -> _state.update { it.copy(busy = false, message = "پیشنهادِ شما ثبت شد") }
                else -> _state.update { it.copy(busy = false, message = "ثبتِ پیشنهاد ناموفق بود") }
            }
        }
    }

    fun startChat(title: String) {
        if (shopId <= 0) return
        viewModelScope.launch {
            when (val res = interaction.startConversation(shopId)) {
                is AppResult.Success -> _effect.send(ProductDetailEffect.OpenChat(res.data.id, title))
                else -> _state.update { it.copy(message = "شروعِ گفت‌وگو ناموفق بود") }
            }
        }
    }

    fun report(reason: String?, productId: Long) {
        viewModelScope.launch {
            when (repository.createReport("PRODUCT", productId, reason)) {
                is AppResult.Success -> _state.update { it.copy(message = "گزارشِ شما ثبت شد") }
                else -> _state.update { it.copy(message = "ثبتِ گزارش ناموفق بود") }
            }
        }
    }

    fun consumeMessage() = _state.update { it.copy(message = null) }
}
