package com.kazemieh.bazaar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kazemieh.common.AppResult
import com.kazemieh.domain.interaction.InteractionRepository
import com.kazemieh.domain.marketplace.MarketplaceRepository
import com.kazemieh.domain.marketplace.Product
import com.kazemieh.domain.marketplace.Review
import com.kazemieh.domain.marketplace.Shop
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ShopDetailState(
    val shop: AppResult<Shop> = AppResult.Loading,
    val products: AppResult<List<Product>> = AppResult.Loading,
    val bookmarkId: Long? = null,          // ≠ null یعنی نشان‌شده
    val bookmarkBusy: Boolean = false,
    val offerSubmitting: Boolean = false,
    val reviews: AppResult<List<Review>> = AppResult.Loading,
    val reviewSubmitting: Boolean = false,
    val orderBusyProductId: Long? = null,
    val message: String? = null,           // پیامِ گذرا (اسنک‌بار)
)

sealed interface ShopDetailEffect {
    data class OpenChat(val conversationId: Long, val title: String) : ShopDetailEffect
}

class ShopDetailViewModel(
    private val repository: MarketplaceRepository,
    private val interaction: InteractionRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ShopDetailState())
    val state = _state.asStateFlow()

    private val _effect = Channel<ShopDetailEffect>()
    val effect = _effect.receiveAsFlow()

    private var loaded = false
    private var shopId: Long = 0

    fun load(shopId: Long) {
        if (loaded) return
        loaded = true
        this.shopId = shopId
        viewModelScope.launch { _state.update { it.copy(shop = repository.getShop(shopId)) } }
        viewModelScope.launch { _state.update { it.copy(products = repository.getProductsByShop(shopId)) } }
        viewModelScope.launch { _state.update { it.copy(reviews = repository.getReviews(shopId)) } }
        viewModelScope.launch {
            when (val res = interaction.getBookmarks()) {
                is AppResult.Success -> {
                    val bm = res.data.firstOrNull { it.shopId == shopId }
                    _state.update { it.copy(bookmarkId = bm?.id) }
                }
                else -> {}
            }
        }
    }

    fun toggleBookmark() {
        if (_state.value.bookmarkBusy) return
        _state.update { it.copy(bookmarkBusy = true) }
        viewModelScope.launch {
            val current = _state.value.bookmarkId
            if (current != null) {
                interaction.removeBookmark(current)
                _state.update { it.copy(bookmarkId = null, bookmarkBusy = false, message = "از نشان‌شده‌ها حذف شد") }
            } else {
                when (val res = interaction.addBookmark(shopId = shopId, productId = null)) {
                    is AppResult.Success -> _state.update { it.copy(bookmarkId = res.data.id, bookmarkBusy = false, message = "نشان شد") }
                    else -> _state.update { it.copy(bookmarkBusy = false, message = "خطا در نشان‌کردن") }
                }
            }
        }
    }

    fun submitOffer(amount: Double, message: String?) {
        _state.update { it.copy(offerSubmitting = true) }
        viewModelScope.launch {
            when (interaction.createOffer(shopId, null, amount, message)) {
                is AppResult.Success -> _state.update { it.copy(offerSubmitting = false, message = "پیشنهادِ شما ثبت شد") }
                is AppResult.Error -> _state.update { it.copy(offerSubmitting = false, message = "ثبتِ پیشنهاد ناموفق بود") }
                is AppResult.Loading -> {}
            }
        }
    }

    fun startChat(title: String) {
        viewModelScope.launch {
            when (val res = interaction.startConversation(shopId)) {
                is AppResult.Success -> _effect.send(ShopDetailEffect.OpenChat(res.data.id, title))
                else -> _state.update { it.copy(message = "شروعِ گفت‌وگو ناموفق بود") }
            }
        }
    }

    fun submitReview(rating: Int, comment: String?) {
        _state.update { it.copy(reviewSubmitting = true) }
        viewModelScope.launch {
            when (repository.createReview(shopId, rating, comment)) {
                is AppResult.Success -> {
                    _state.update { it.copy(reviewSubmitting = false, message = "نظرِ شما ثبت شد") }
                    _state.update { it.copy(reviews = repository.getReviews(shopId)) }
                    _state.update { it.copy(shop = repository.getShop(shopId)) }
                }
                else -> _state.update { it.copy(reviewSubmitting = false, message = "ثبتِ نظر ناموفق بود") }
            }
        }
    }

    /** خریدِ سریعِ یک کالا (تعداد ۱) — سفارشِ تک‌ونـدوری. */
    fun quickOrder(product: Product) {
        if (_state.value.orderBusyProductId != null) return
        _state.update { it.copy(orderBusyProductId = product.id) }
        viewModelScope.launch {
            when (repository.createOrder(shopId, listOf(product.id to 1), null)) {
                is AppResult.Success -> {
                    _state.update { it.copy(orderBusyProductId = null, message = "سفارشِ شما ثبت شد") }
                    _state.update { it.copy(products = repository.getProductsByShop(shopId)) }
                }
                else -> _state.update { it.copy(orderBusyProductId = null, message = "ثبتِ سفارش ناموفق بود") }
            }
        }
    }

    fun consumeMessage() = _state.update { it.copy(message = null) }
}
