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

/** خلاصهٔ داشبوردِ نقش‌ها برای تبِ پروفایل (فروشنده + ادمین). */
data class ProfileDashboardState(
    // فروشنده
    val vendorShop: Shop? = null,
    val myProducts: List<Product> = emptyList(),
    val vendorOrderCount: Int = 0,
    // ادمین
    val pendingShops: List<Shop> = emptyList(),
    val activeShopCount: Int = 0,
    val openReportCount: Int = 0,
    val busyShopId: Long? = null,
    val message: String? = null,
)

class ProfileDashboardViewModel(
    private val repository: MarketplaceRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileDashboardState())
    val state = _state.asStateFlow()

    private var loaded = false

    fun load(isVendor: Boolean, isAdmin: Boolean) {
        if (loaded) return
        loaded = true
        if (isVendor) loadVendor()
        if (isAdmin) loadAdmin()
    }

    private fun loadVendor() {
        viewModelScope.launch {
            val shop = (repository.getMyShops() as? AppResult.Success)?.data?.firstOrNull() ?: return@launch
            _state.update { it.copy(vendorShop = shop) }
            (repository.getMyShopProducts(shop.id) as? AppResult.Success)?.let { r ->
                _state.update { it.copy(myProducts = r.data) }
            }
            (repository.getVendorOrders(shop.id) as? AppResult.Success)?.let { r ->
                _state.update { it.copy(vendorOrderCount = r.data.size) }
            }
        }
    }

    private fun loadAdmin() {
        viewModelScope.launch {
            (repository.getAdminShops("PENDING") as? AppResult.Success)?.let { r ->
                _state.update { it.copy(pendingShops = r.data) }
            }
            (repository.getAdminShops("APPROVED") as? AppResult.Success)?.let { r ->
                _state.update { it.copy(activeShopCount = r.data.size) }
            }
            (repository.getAdminReports("OPEN") as? AppResult.Success)?.let { r ->
                _state.update { it.copy(openReportCount = r.data.size) }
            }
        }
    }

    fun approve(id: Long) = act(id) { repository.approveShop(id) }
    fun reject(id: Long) = act(id) { repository.rejectShop(id) }

    private fun act(id: Long, block: suspend () -> AppResult<Shop>) {
        _state.update { it.copy(busyShopId = id) }
        viewModelScope.launch {
            when (block()) {
                is AppResult.Success -> _state.update {
                    it.copy(busyShopId = null, pendingShops = it.pendingShops.filterNot { s -> s.id == id }, message = "انجام شد")
                }
                is AppResult.Error -> _state.update { it.copy(busyShopId = null, message = "خطا در انجامِ عملیات") }
                else -> _state.update { it.copy(busyShopId = null) }
            }
        }
    }

    fun consumeMessage() = _state.update { it.copy(message = null) }
}
