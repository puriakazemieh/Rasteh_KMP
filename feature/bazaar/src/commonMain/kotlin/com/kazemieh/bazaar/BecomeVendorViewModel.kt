package com.kazemieh.bazaar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kazemieh.common.AppResult
import com.kazemieh.domain.marketplace.MarketplaceRepository
import com.kazemieh.domain.marketplace.Rasteh
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BecomeVendorViewModel(
    private val repository: MarketplaceRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(BecomeVendorState())
    val state = _state.asStateFlow()

    init {
        loadRastehs()
    }

    fun handleIntent(intent: BecomeVendorIntent) {
        when (intent) {
            is BecomeVendorIntent.OnName -> _state.update { it.copy(name = intent.value) }
            is BecomeVendorIntent.OnRasteh -> onRasteh(intent.value)
            is BecomeVendorIntent.OnLocation -> _state.update { it.copy(selectedLocation = intent.value) }
            is BecomeVendorIntent.OnCategory -> _state.update { it.copy(category = intent.value) }
            is BecomeVendorIntent.OnFloor -> _state.update { it.copy(floor = intent.value) }
            is BecomeVendorIntent.OnType -> _state.update { it.copy(type = intent.value) }
            is BecomeVendorIntent.OnPhone -> _state.update { it.copy(phone = intent.value) }
            is BecomeVendorIntent.OnAbout -> _state.update { it.copy(about = intent.value) }
            is BecomeVendorIntent.OnHasChat -> _state.update { it.copy(hasChat = intent.value) }
            is BecomeVendorIntent.OnAcceptsOffers -> _state.update { it.copy(acceptsOffers = intent.value) }
            is BecomeVendorIntent.Submit -> submit()
        }
    }

    private fun loadRastehs() {
        viewModelScope.launch {
            when (val result = repository.getRastehs()) {
                is AppResult.Success -> _state.update { it.copy(rastehs = result.data) }
                is AppResult.Error -> _state.update { it.copy(error = "دریافتِ راسته‌ها ناموفق بود") }
                is AppResult.Loading -> {}
            }
        }
    }

    private fun onRasteh(rasteh: Rasteh) {
        _state.update {
            it.copy(
                selectedRasteh = rasteh,
                selectedLocation = null,
                locations = emptyList(),
                isLocationsLoading = true,
            )
        }
        viewModelScope.launch {
            val result = repository.getLocationsByRasteh(rasteh.id)
            _state.update {
                it.copy(
                    isLocationsLoading = false,
                    locations = if (result is AppResult.Success) result.data else emptyList(),
                )
            }
        }
    }

    private fun submit() {
        val s = _state.value
        if (!s.canSubmit) return
        _state.update { it.copy(isSubmitting = true, error = null) }
        viewModelScope.launch {
            val result = repository.registerShop(
                name = s.name.trim(),
                rastehId = s.selectedRasteh?.id,
                locationId = s.selectedLocation?.id,
                category = s.category.trim().ifBlank { null },
                floor = s.floor.trim().ifBlank { null },
                type = s.type,
                phone = s.phone.trim().ifBlank { null },
                address = null,
                workingHoursJson = null,
                about = s.about.trim().ifBlank { null },
                hasChat = s.hasChat,
                acceptsOffers = s.acceptsOffers,
                emoji = null,
                coverStyle = null,
            )
            when (result) {
                is AppResult.Success -> _state.update { it.copy(isSubmitting = false, submitted = true) }
                is AppResult.Error -> _state.update {
                    it.copy(isSubmitting = false, error = "ثبتِ درخواست ناموفق بود؛ دوباره تلاش کنید")
                }
                is AppResult.Loading -> {}
            }
        }
    }
}
