package com.kazemieh.bazaar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kazemieh.common.AppResult
import com.kazemieh.domain.marketplace.MarketplaceRepository
import com.kazemieh.domain.marketplace.Shop
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SearchState(
    val query: String = "",
    val results: AppResult<List<Shop>>? = null,
)

class SearchViewModel(
    private val repository: MarketplaceRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(SearchState())
    val state = _state.asStateFlow()

    fun onQuery(value: String) = _state.update { it.copy(query = value) }

    fun search() {
        val q = _state.value.query.trim()
        _state.update { it.copy(results = AppResult.Loading) }
        viewModelScope.launch {
            _state.update { it.copy(results = repository.searchShops(q.ifBlank { null }, null, null, null, "rating")) }
        }
    }
}
