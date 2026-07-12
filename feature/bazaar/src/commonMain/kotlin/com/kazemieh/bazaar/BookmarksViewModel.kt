package com.kazemieh.bazaar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kazemieh.common.AppResult
import com.kazemieh.domain.interaction.Bookmark
import com.kazemieh.domain.interaction.InteractionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BookmarksViewModel(
    private val interaction: InteractionRepository,
) : ViewModel() {
    private val _state = MutableStateFlow<AppResult<List<Bookmark>>>(AppResult.Loading)
    val state = _state.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch { _state.value = interaction.getBookmarks() }
    }

    fun remove(id: Long) {
        viewModelScope.launch {
            interaction.removeBookmark(id)
            load()
        }
    }
}
