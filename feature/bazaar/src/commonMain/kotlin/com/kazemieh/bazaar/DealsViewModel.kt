package com.kazemieh.bazaar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kazemieh.common.AppResult
import com.kazemieh.domain.features.FeaturesRepository
import com.kazemieh.domain.features.FlashSale
import com.kazemieh.domain.features.GroupBuy
import com.kazemieh.domain.features.Loyalty
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DealsState(
    val flash: AppResult<List<FlashSale>> = AppResult.Loading,
    val groupBuys: AppResult<List<GroupBuy>> = AppResult.Loading,
    val loyalty: Loyalty? = null,
    val joiningId: Long? = null,
    val message: String? = null,
)

class DealsViewModel(
    private val repository: FeaturesRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(DealsState())
    val state = _state.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch { _state.update { it.copy(flash = repository.getFlashSales()) } }
        viewModelScope.launch { _state.update { it.copy(groupBuys = repository.getGroupBuys()) } }
        viewModelScope.launch {
            val res = repository.getLoyalty()
            if (res is AppResult.Success) _state.update { it.copy(loyalty = res.data) }
        }
    }

    fun join(id: Long) {
        if (_state.value.joiningId != null) return
        _state.update { it.copy(joiningId = id) }
        viewModelScope.launch {
            when (repository.joinGroupBuy(id)) {
                is AppResult.Success -> {
                    _state.update { it.copy(joiningId = null, message = "به خریدِ گروهی پیوستید") }
                    _state.update { it.copy(groupBuys = repository.getGroupBuys()) }
                }
                else -> _state.update { it.copy(joiningId = null, message = "پیوستن ناموفق بود") }
            }
        }
    }

    fun consumeMessage() = _state.update { it.copy(message = null) }
}
