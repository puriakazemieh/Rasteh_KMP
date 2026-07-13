package com.kazemieh.bazaar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kazemieh.common.AppResult
import com.kazemieh.domain.advanced.AdvancedRepository
import com.kazemieh.domain.advanced.Post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CommunityState(
    val posts: AppResult<List<Post>> = AppResult.Loading,
    val input: String = "",
    val posting: Boolean = false,
)

class CommunityViewModel(
    private val repository: AdvancedRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(CommunityState())
    val state = _state.asStateFlow()

    init { load() }

    fun load() { viewModelScope.launch { _state.update { it.copy(posts = repository.getPosts()) } } }
    fun onInput(v: String) = _state.update { it.copy(input = v) }
    fun post() {
        val body = _state.value.input.trim()
        if (body.isEmpty() || _state.value.posting) return
        _state.update { it.copy(posting = true, input = "") }
        viewModelScope.launch {
            repository.createPost(body)
            _state.update { it.copy(posting = false) }
            load()
        }
    }
}
