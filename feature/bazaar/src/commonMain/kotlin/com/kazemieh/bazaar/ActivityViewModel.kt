package com.kazemieh.bazaar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kazemieh.common.AppResult
import com.kazemieh.domain.advanced.ActivityItem
import com.kazemieh.domain.advanced.AdvancedRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ActivityState(
    val items: AppResult<List<ActivityItem>> = AppResult.Loading,
)

/** فیدِ فعالیت (activity) · نشان‌کردن‌ها و سفارش‌های اخیرِ کاربر. */
class ActivityViewModel(
    private val repository: AdvancedRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(ActivityState())
    val state = _state.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch { _state.update { it.copy(items = repository.getActivity()) } }
    }
}
