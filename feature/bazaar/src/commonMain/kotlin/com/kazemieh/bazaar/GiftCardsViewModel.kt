package com.kazemieh.bazaar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kazemieh.common.AppResult
import com.kazemieh.domain.services.GiftCard
import com.kazemieh.domain.services.ServicesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GiftCardsState(
    val cards: AppResult<List<GiftCard>> = AppResult.Loading,
    val busy: Boolean = false,
    val message: String? = null,
)

class GiftCardsViewModel(
    private val repository: ServicesRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(GiftCardsState())
    val state = _state.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch { _state.update { it.copy(cards = repository.getGiftCards()) } }
    }

    fun issue(amount: Double) {
        if (_state.value.busy) return
        _state.update { it.copy(busy = true) }
        viewModelScope.launch {
            when (repository.issueGiftCard(amount)) {
                is AppResult.Success -> { _state.update { it.copy(busy = false, message = "کارتِ هدیه ساخته شد") }; load() }
                else -> _state.update { it.copy(busy = false, message = "ساختِ کارت ناموفق بود") }
            }
        }
    }

    fun redeem(code: String) {
        if (_state.value.busy || code.isBlank()) return
        _state.update { it.copy(busy = true) }
        viewModelScope.launch {
            when (repository.redeemGiftCard(code.trim())) {
                is AppResult.Success -> { _state.update { it.copy(busy = false, message = "کارت به حسابِ شما افزوده شد") }; load() }
                else -> _state.update { it.copy(busy = false, message = "کدِ کارت نامعتبر است") }
            }
        }
    }

    fun consumeMessage() = _state.update { it.copy(message = null) }
}
