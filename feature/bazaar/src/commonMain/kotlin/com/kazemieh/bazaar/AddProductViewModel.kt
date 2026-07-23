package com.kazemieh.bazaar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kazemieh.common.AppResult
import com.kazemieh.domain.marketplace.MarketplaceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** حالتِ فرمِ «افزودن محصول جدید» — مطابقِ بستهٔ طراحی. */
data class AddProductState(
    val loadingShop: Boolean = true,
    val shopId: Long? = null,
    val shopName: String = "",
    val shopBuyable: Boolean = false,
    val title: String = "",
    val price: String = "",
    val category: String = "",
    val condition: String = "NEW",   // NEW | USED
    val stock: String = "",
    val discount: String = "",
    val description: String = "",
    val submitting: Boolean = false,
    val added: Boolean = false,
    val error: String? = null,
)

class AddProductViewModel(
    private val repository: MarketplaceRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(AddProductState())
    val state = _state.asStateFlow()

    init { loadShop() }

    private fun loadShop() {
        viewModelScope.launch {
            when (val res = repository.getMyShops()) {
                is AppResult.Success -> {
                    val shop = res.data.firstOrNull()
                    _state.update {
                        it.copy(
                            loadingShop = false,
                            shopId = shop?.id,
                            shopName = shop?.name ?: "فروشگاهِ من",
                            shopBuyable = shop?.type == "ONLINE" || shop?.type == "BUYABLE",
                            error = if (shop == null) "ابتدا باید فروشگاهی داشته باشید" else null,
                        )
                    }
                }
                is AppResult.Error -> _state.update { it.copy(loadingShop = false, error = res.message) }
                else -> {}
            }
        }
    }

    fun onTitle(v: String) = _state.update { it.copy(title = v) }
    fun onPrice(v: String) = _state.update { it.copy(price = v.filter { c -> c.isDigit() }) }
    fun onCategory(v: String) = _state.update { it.copy(category = v) }
    fun onCondition(v: String) = _state.update { it.copy(condition = v) }
    fun onStock(v: String) = _state.update { it.copy(stock = v.filter { c -> c.isDigit() }) }
    fun onDiscount(v: String) = _state.update { it.copy(discount = v.filter { c -> c.isDigit() }) }
    fun onDescription(v: String) = _state.update { it.copy(description = v) }

    val canSubmit: Boolean
        get() = state.value.let { it.shopId != null && it.title.isNotBlank() && it.price.isNotBlank() && !it.submitting }

    fun submit() {
        val s = state.value
        val shopId = s.shopId ?: return
        val priceValue = s.price.toDoubleOrNull() ?: return
        if (s.title.isBlank()) return
        _state.update { it.copy(submitting = true, error = null) }
        viewModelScope.launch {
            val res = repository.createProduct(
                shopId = shopId,
                name = s.title.trim(),
                description = s.description.ifBlank { null },
                price = priceValue,
                oldPrice = null,
                discountPercent = s.discount.toIntOrNull()?.takeIf { it > 0 },
                condition = s.condition,
                stock = s.stock.toIntOrNull() ?: 0,
                categoryName = s.category.ifBlank { null },
                emoji = null,
                imageUrl = null,
            )
            when (res) {
                is AppResult.Success -> _state.update { it.copy(submitting = false, added = true) }
                is AppResult.Error -> _state.update { it.copy(submitting = false, error = res.message) }
                else -> _state.update { it.copy(submitting = false) }
            }
        }
    }

    fun reset() = _state.update {
        AddProductState(loadingShop = false, shopId = it.shopId, shopName = it.shopName, shopBuyable = it.shopBuyable)
    }
}
