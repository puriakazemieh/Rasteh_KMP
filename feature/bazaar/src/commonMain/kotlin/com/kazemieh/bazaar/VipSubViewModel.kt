package com.kazemieh.bazaar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kazemieh.common.AppResult
import com.kazemieh.domain.advanced.AdvancedRepository
import com.kazemieh.domain.advanced.Subscription
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class VipSubState(
    val subscription: Subscription? = null,
    val loading: Boolean = true,
    val busy: Boolean = false,
    val message: String? = null,
)

/** «بازارچه پلاس» (vipsub): وضعیتِ اشتراک + خرید/تمدید. */
class VipSubViewModel(
    private val repository: AdvancedRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(VipSubState())
    val state = _state.asStateFlow()

    init { load() }

    fun load() {
        _state.update { it.copy(loading = true) }
        viewModelScope.launch {
            val res = repository.getSubscription()
            _state.update {
                it.copy(loading = false, subscription = (res as? AppResult.Success)?.data)
            }
        }
    }

    fun subscribe() {
        if (_state.value.busy) return
        _state.update { it.copy(busy = true) }
        viewModelScope.launch {
            when (val res = repository.subscribe()) {
                is AppResult.Success -> _state.update { it.copy(busy = false, subscription = res.data, message = "اشتراکِ بازارچه پلاس فعال شد") }
                else -> _state.update { it.copy(busy = false, message = "فعال‌سازیِ اشتراک ناموفق بود") }
            }
        }
    }

    fun consumeMessage() = _state.update { it.copy(message = null) }
}
