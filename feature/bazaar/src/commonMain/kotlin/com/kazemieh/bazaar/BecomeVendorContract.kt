package com.kazemieh.bazaar

import com.kazemieh.domain.marketplace.MarketplaceLocation
import com.kazemieh.domain.marketplace.Rasteh

/**
 * وضعیتِ فرمِ «درخواستِ فروشندگی» (becomeVendor). با ثبتِ موفق، فروشگاه در وضعیتِ PENDING
 * ساخته می‌شود و منتظرِ تأییدِ ادمین می‌ماند.
 */
data class BecomeVendorState(
    val rastehs: List<Rasteh> = emptyList(),
    val locations: List<MarketplaceLocation> = emptyList(),
    val isLocationsLoading: Boolean = false,

    val name: String = "",
    val selectedRasteh: Rasteh? = null,
    val selectedLocation: MarketplaceLocation? = null,
    val category: String = "",
    val floor: String = "",
    val type: String = "BUYABLE", // BUYABLE | VISIT_ONLY
    val phone: String = "",
    val about: String = "",
    val hasChat: Boolean = true,
    val acceptsOffers: Boolean = false,

    val isSubmitting: Boolean = false,
    val submitted: Boolean = false,
    val error: String? = null,
) {
    val canSubmit: Boolean
        get() = name.isNotBlank() && selectedRasteh != null && selectedLocation != null && !isSubmitting
}

sealed interface BecomeVendorIntent {
    data class OnName(val value: String) : BecomeVendorIntent
    data class OnRasteh(val value: Rasteh) : BecomeVendorIntent
    data class OnLocation(val value: MarketplaceLocation) : BecomeVendorIntent
    data class OnCategory(val value: String) : BecomeVendorIntent
    data class OnFloor(val value: String) : BecomeVendorIntent
    data class OnType(val value: String) : BecomeVendorIntent
    data class OnPhone(val value: String) : BecomeVendorIntent
    data class OnAbout(val value: String) : BecomeVendorIntent
    data class OnHasChat(val value: Boolean) : BecomeVendorIntent
    data class OnAcceptsOffers(val value: Boolean) : BecomeVendorIntent
    data object Submit : BecomeVendorIntent
}
