package com.kazemieh.bazaar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kazemieh.common.AppResult
import com.kazemieh.domain.interaction.InteractionRepository
import com.kazemieh.domain.marketplace.MarketplaceRepository
import com.kazemieh.domain.marketplace.Rasteh
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RastehHomeViewModel(
    private val repository: MarketplaceRepository,
    private val interaction: InteractionRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(RastehHomeState())
    val state = _state.asStateFlow()

    private val _effect = Channel<RastehHomeEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        loadRastehs()
        loadNewest()
        loadBookmarks()
    }

    fun handleIntent(intent: RastehHomeIntent) {
        when (intent) {
            is RastehHomeIntent.LoadRastehs -> loadRastehs()
            is RastehHomeIntent.OnQueryChange -> _state.update { it.copy(query = intent.value) }
            is RastehHomeIntent.OnHomeTab -> _state.update { it.copy(homeTab = intent.tab) }
            is RastehHomeIntent.OnRastehClick -> openSheet(intent.rasteh)
            is RastehHomeIntent.DismissSheet ->
                _state.update { it.copy(sheetRasteh = null, locations = AppResult.Loading) }
            is RastehHomeIntent.OnLocationClick -> {
                val rasteh = _state.value.sheetRasteh
                if (rasteh != null) {
                    viewModelScope.launch {
                        _effect.send(
                            RastehHomeEffect.NavigateToRastehSearch(
                                rastehId = rasteh.id,
                                rastehLabel = rasteh.label,
                                locationId = intent.location.id,
                                locationName = intent.location.name,
                            )
                        )
                    }
                    _state.update { it.copy(sheetRasteh = null, locations = AppResult.Loading) }
                }
            }
        }
    }

    private fun loadRastehs() {
        viewModelScope.launch {
            _state.update { it.copy(rastehs = AppResult.Loading) }
            _state.update { it.copy(rastehs = repository.getRastehs()) }
        }
    }

    private fun loadNewest() {
        viewModelScope.launch {
            _state.update { it.copy(newestShops = repository.searchShops(null, null, null, null, "newest")) }
        }
        viewModelScope.launch {
            _state.update { it.copy(newestProducts = repository.searchProducts(null, null, null, null, null, "newest")) }
        }
    }

    private fun loadBookmarks() {
        viewModelScope.launch {
            when (val res = interaction.getBookmarks()) {
                is AppResult.Success -> _state.update { it.copy(bookmarks = res.data) }
                else -> {}
            }
        }
    }

    private fun openSheet(rasteh: Rasteh) {
        _state.update { it.copy(sheetRasteh = rasteh, locations = AppResult.Loading) }
        viewModelScope.launch {
            _state.update { it.copy(locations = repository.getLocationsByRasteh(rasteh.id)) }
        }
    }
}
