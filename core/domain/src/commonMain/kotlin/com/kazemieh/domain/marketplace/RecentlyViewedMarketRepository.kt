package com.kazemieh.domain.marketplace

import kotlinx.coroutines.flow.Flow

/**
 * تاریخچهٔ محصولاتِ بازارچه (راسته) که کاربر اخیراً دیده است.
 *
 * برخلافِ recently-viewedِ کاتالوگ که روی سرور نگه‌داری می‌شود، این تاریخچه برای
 * محصولاتِ مارکت‌پلیس است و به‌صورتِ محلی (multiplatform-settings) روی همان دستگاه
 * نگه‌داری می‌شود؛ نیازی به اندپوینتِ سرور ندارد و آفلاین هم کار می‌کند.
 */
interface RecentlyViewedMarketRepository {
    fun observe(): Flow<List<Product>>
    suspend fun add(product: Product)
    suspend fun clear()
}
