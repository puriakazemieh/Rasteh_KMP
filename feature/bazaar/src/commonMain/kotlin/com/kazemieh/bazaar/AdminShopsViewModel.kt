package com.kazemieh.bazaar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kazemieh.common.AppResult
import com.kazemieh.domain.marketplace.MarketplaceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AdminShopsViewModel(
    private val repository: MarketplaceRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(AdminShopsState())
    val state = _state.asStateFlow()

    init {
        load()
    }

    fun handleIntent(intent: AdminShopsIntent) {
        when (intent) {
            is AdminShopsIntent.OnStatusChange -> {
                _state.update { it.copy(status = intent.status, shops = AppResult.Loading) }
                load()
            }
            is AdminShopsIntent.Refresh -> load()
            is AdminShopsIntent.Approve -> act(intent.id) { repository.approveShop(it) }
            is AdminShopsIntent.Reject -> act(intent.id) { repository.rejectShop(it) }
            is AdminShopsIntent.Suspend -> act(intent.id) { repository.suspendShop(it) }
        }
    }

    private fun load() {
        viewModelScope.launch {
            _state.update { it.copy(shops = repository.getAdminShops(_state.value.status)) }
        }
    }

    private fun act(id: Long, action: suspend (Long) -> AppResult<*>) {
        _state.update { it.copy(actingId = id) }
        viewModelScope.launch {
            action(id)
            _state.update { it.copy(actingId = null) }
            load()
        }
    }
}
