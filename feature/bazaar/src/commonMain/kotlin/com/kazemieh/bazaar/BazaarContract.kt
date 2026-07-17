package com.kazemieh.bazaar

import com.kazemieh.common.AppResult
import com.kazemieh.domain.interaction.Bookmark
import com.kazemieh.domain.marketplace.MarketplaceLocation
import com.kazemieh.domain.marketplace.Product
import com.kazemieh.domain.marketplace.Rasteh
import com.kazemieh.domain.marketplace.Shop

enum class HomeTab { SHOPS, PRODUCTS }

/**
 * وضعیتِ خانهٔ v3 (design_handoff_final): سرچ + گریدِ راسته + تب‌های فروشگاه/محصول
 * + نشان‌شده‌ها + جدیدترین فروشگاه/محصول.
 */
data class RastehHomeState(
    val rastehs: AppResult<List<Rasteh>> = AppResult.Loading,
    val query: String = "",
    val homeTab: HomeTab = HomeTab.SHOPS,
    val newestShops: AppResult<List<Shop>> = AppResult.Loading,
    val newestProducts: AppResult<List<Product>> = AppResult.Loading,
    val recentProducts: List<Product> = emptyList(),
    val bookmarks: List<Bookmark> = emptyList(),
    // باتم‌شیتِ انتخابِ محل برای راستهٔ انتخاب‌شده
    val sheetRasteh: Rasteh? = null,
    val locations: AppResult<List<MarketplaceLocation>> = AppResult.Loading,
)

sealed interface RastehHomeIntent {
    data object LoadRastehs : RastehHomeIntent
    data class OnQueryChange(val value: String) : RastehHomeIntent
    data class OnHomeTab(val tab: HomeTab) : RastehHomeIntent
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
