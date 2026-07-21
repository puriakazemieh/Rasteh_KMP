package com.kazemieh.bazaar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kazemieh.common.AppResult
import com.kazemieh.domain.marketplace.MarketplaceLocation
import com.kazemieh.domain.marketplace.MarketplaceRepository
import com.kazemieh.domain.marketplace.Shop
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WayfindState(
    val location: MarketplaceLocation? = null,
    val shops: AppResult<List<Shop>> = AppResult.Loading,
)

/** مسیریابِ داخلِ محل (wayfind/floorMap): محل + فروشگاه‌هایش، گروه‌بندی‌شده بر اساسِ طبقه. */
class WayfindViewModel(
    private val repository: MarketplaceRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(WayfindState())
    val state = _state.asStateFlow()

    private var loadedFor: Long? = null

    fun load(locationId: Long) {
        if (loadedFor == locationId) return
        loadedFor = locationId
        viewModelScope.launch {
            val loc = repository.getLocation(locationId)
            if (loc is AppResult.Success) _state.update { it.copy(location = loc.data) }
        }
        viewModelScope.launch {
            _state.update { it.copy(shops = repository.getShopsByLocation(locationId, null)) }
        }
    }
}
