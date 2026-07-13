package com.kazemieh.bazaar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kazemieh.common.AppResult
import com.kazemieh.domain.advanced.AdvancedRepository
import com.kazemieh.domain.advanced.NotificationItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NotificationsViewModel(
    private val repository: AdvancedRepository,
) : ViewModel() {
    private val _state = MutableStateFlow<AppResult<List<NotificationItem>>>(AppResult.Loading)
    val state = _state.asStateFlow()

    init { load() }

    fun load() { viewModelScope.launch { _state.value = repository.getNotifications() } }
    fun markRead(id: Long) { viewModelScope.launch { repository.markNotificationRead(id); load() } }
}
