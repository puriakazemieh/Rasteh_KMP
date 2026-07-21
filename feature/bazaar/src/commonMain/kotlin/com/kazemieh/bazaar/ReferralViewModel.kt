package com.kazemieh.bazaar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kazemieh.common.AppResult
import com.kazemieh.domain.advanced.AdvancedRepository
import com.kazemieh.domain.advanced.Referral
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ReferralState(
    val referral: Referral? = null,
    val loading: Boolean = true,
    val busy: Boolean = false,
    val message: String? = null,
)

/** دعوتِ دوستان (referral) — کدِ دعوتِ کاربر + ثبتِ کدِ معرف. */
class ReferralViewModel(
    private val repository: AdvancedRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(ReferralState())
    val state = _state.asStateFlow()

    init { load() }

    fun load() {
        _state.update { it.copy(loading = true) }
        viewModelScope.launch {
            val res = repository.getMyReferral()
            _state.update { it.copy(loading = false, referral = (res as? AppResult.Success)?.data) }
        }
    }

    fun redeem(code: String) {
        if (_state.value.busy || code.isBlank()) return
        _state.update { it.copy(busy = true) }
        viewModelScope.launch {
            when (val res = repository.redeemReferral(code.trim().uppercase())) {
                is AppResult.Success -> _state.update { it.copy(busy = false, referral = res.data, message = "کدِ معرف ثبت شد") }
                else -> _state.update { it.copy(busy = false, message = "کدِ معرف نامعتبر است یا قبلاً ثبت شده") }
            }
        }
    }

    fun consumeMessage() = _state.update { it.copy(message = null) }
}
