package com.kazemieh.bazaar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kazemieh.common.AppResult
import com.kazemieh.domain.advanced.AdvancedRepository
import com.kazemieh.domain.advanced.VendorAnalytics
import com.kazemieh.domain.interaction.InteractionRepository
import com.kazemieh.domain.interaction.Offer
import com.kazemieh.domain.marketplace.MarketplaceRepository
import com.kazemieh.domain.marketplace.Order
import com.kazemieh.domain.marketplace.Product
import com.kazemieh.domain.marketplace.Shop
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class VendorPanelState(
    val shops: AppResult<List<Shop>> = AppResult.Loading,
    val shopId: Long? = null,
    val shopName: String = "",
    val tab: Int = 0,   // 0 داشبورد · 1 محصولات · 2 سفارش‌ها · 3 پیشنهادها
    val analytics: VendorAnalytics? = null,
    val products: AppResult<List<Product>> = AppResult.Loading,
    val orders: AppResult<List<Order>> = AppResult.Loading,
    val offers: AppResult<List<Offer>> = AppResult.Loading,
    val busy: Boolean = false,
    val message: String? = null,
)

class VendorPanelViewModel(
    private val repository: MarketplaceRepository,
    private val interaction: InteractionRepository,
    private val advanced: AdvancedRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(VendorPanelState())
    val state = _state.asStateFlow()

    init { loadShops() }

    private fun loadShops() {
        viewModelScope.launch {
            val res = repository.getMyShops()
            _state.update { it.copy(shops = res) }
            if (res is AppResult.Success) {
                res.data.firstOrNull()?.let { selectShop(it.id, it.name) }
            }
        }
    }

    fun selectShop(shopId: Long, name: String) {
        _state.update { it.copy(shopId = shopId, shopName = name) }
        loadShopData(shopId)
    }

    fun onTab(tab: Int) = _state.update { it.copy(tab = tab) }

    private fun loadShopData(shopId: Long) {
        viewModelScope.launch {
            val res = advanced.getVendorAnalytics(shopId)
            if (res is AppResult.Success) _state.update { it.copy(analytics = res.data) }
        }
        viewModelScope.launch { _state.update { it.copy(products = repository.getMyShopProducts(shopId)) } }
        viewModelScope.launch { _state.update { it.copy(orders = repository.getVendorOrders(shopId)) } }
        viewModelScope.launch { _state.update { it.copy(offers = interaction.getShopOffers(shopId)) } }
    }

    fun createProduct(name: String, price: Double, stock: Int, condition: String) {
        val shopId = _state.value.shopId ?: return
        if (name.isBlank() || _state.value.busy) return
        _state.update { it.copy(busy = true) }
        viewModelScope.launch {
            when (repository.createProduct(shopId, name.trim(), null, price, null, null, condition, stock, null, null, null)) {
                is AppResult.Success -> { _state.update { it.copy(busy = false, message = "محصول اضافه شد") }; _state.update { it.copy(products = repository.getMyShopProducts(shopId)) } }
                else -> _state.update { it.copy(busy = false, message = "افزودنِ محصول ناموفق بود") }
            }
        }
    }

    fun deleteProduct(id: Long) {
        val shopId = _state.value.shopId ?: return
        viewModelScope.launch {
            repository.deleteProduct(id)
            _state.update { it.copy(products = repository.getMyShopProducts(shopId)) }
        }
    }

    fun acceptOffer(id: Long) = actOffer { interaction.acceptOffer(id) }
    fun rejectOffer(id: Long) = actOffer { interaction.rejectOffer(id) }

    private fun actOffer(action: suspend () -> AppResult<*>) {
        val shopId = _state.value.shopId ?: return
        viewModelScope.launch {
            action()
            _state.update { it.copy(offers = interaction.getShopOffers(shopId)) }
        }
    }

    fun updateOrderStatus(orderId: Long, status: String) {
        val shopId = _state.value.shopId ?: return
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, status)
            _state.update { it.copy(orders = repository.getVendorOrders(shopId)) }
        }
    }

    fun consumeMessage() = _state.update { it.copy(message = null) }
}
