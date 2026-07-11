package com.kazemieh.bazaar

import com.kazemieh.common.AppResult
import com.kazemieh.domain.marketplace.MarketplaceLocation
import com.kazemieh.domain.marketplace.Rasteh

/**
 * وضعیتِ خانهٔ v2 (search-first): گریدِ راسته‌ها + باتم‌شیتِ انتخابِ محل.
 */
data class RastehHomeState(
    val rastehs: AppResult<List<Rasteh>> = AppResult.Loading,
    val query: String = "",
    // باتم‌شیتِ انتخابِ محل برای راستهٔ انتخاب‌شده
    val sheetRasteh: Rasteh? = null,
    val locations: AppResult<List<MarketplaceLocation>> = AppResult.Loading,
)

sealed interface RastehHomeIntent {
    data object LoadRastehs : RastehHomeIntent
    data class OnQueryChange(val value: String) : RastehHomeIntent
    data class OnRastehClick(val rasteh: Rasteh) : RastehHomeIntent
    data object DismissSheet : RastehHomeIntent
    data class OnLocationClick(val location: MarketplaceLocation) : RastehHomeIntent
}

sealed interface RastehHomeEffect {
    /** انتخابِ محل → صفحهٔ فهرستِ فروشگاه‌های آن راسته در آن محل. */
    data class NavigateToRastehSearch(
        val rastehId: Long,
        val rastehLabel: String,
        val locationId: Long,
        val locationName: String,
    ) : RastehHomeEffect
}
