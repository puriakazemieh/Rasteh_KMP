package com.kazemieh.designsystem.brand

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.kazemieh.designsystem.AccentLight
import com.kazemieh.designsystem.Accent2Light
import com.kazemieh.designsystem.AccentSoftLight
import com.kazemieh.designsystem.GoldLight
import com.kazemieh.designsystem.AccentDark
import com.kazemieh.designsystem.Accent2Dark
import com.kazemieh.designsystem.AccentSoftDark
import com.kazemieh.designsystem.GoldDark

/**
 * معماریِ وایت‌لیبل/برند — منتقل و سبک‌شده از پایهٔ به‌روزِ اپِ فروشگاه
 * (برنچِ admin-profile-pages-redesign). برای راسته پرچم‌های عمودی‌های فروشگاه
 * (آموزشگاه/مشاوره/…) حذف و پرچم‌های مرتبط با مارکت‌پلیس جایگزین شده‌اند.
 */

/** بذرِ رنگیِ یک برند برای یک تم. */
data class BrandPalette(
    val accent: Color,
    val accent2: Color,
    val accentSoft: Color,
    val gold: Color,
    val onAccent: Color = Color.White,
)

data class BrandColors(val light: BrandPalette, val dark: BrandPalette)

/**
 * پرچم‌های قابلیتِ per برند برای گیت‌کردنِ بخش‌های UI (وایت‌لیبلِ آینده).
 * پیش‌فرضِ راسته همه روشن است.
 */
data class BrandFeatures(
    val chat: Boolean = true,
    val offers: Boolean = true,
    val bookmarks: Boolean = true,
    val reviews: Boolean = true,
    /** فلش/خریدِ گروهی/امتیازِ وفاداری. */
    val deals: Boolean = true,
    val priceAlerts: Boolean = true,
    val giftCards: Boolean = true,
    val community: Boolean = true,
    val notifications: Boolean = true,
    val vendorOnboarding: Boolean = true,
)

/** پیکربندیِ کاملِ یک برند/بازارچه. */
data class BrandConfig(
    val id: String,
    val appName: String,
    val colors: BrandColors,
    val currency: String = "تومان",
    val apiBaseUrl: String? = null,
    val features: BrandFeatures = BrandFeatures(),
)

/** برندِ پیش‌فرض: راسته (پالتِ بنفشِ مارکت‌پلیس — همان توکن‌های oklch دیزاین‌سیستم). */
val RastehBrandColors = BrandColors(
    light = BrandPalette(accent = AccentLight, accent2 = Accent2Light, accentSoft = AccentSoftLight, gold = GoldLight, onAccent = Color.White),
    dark = BrandPalette(accent = AccentDark, accent2 = Accent2Dark, accentSoft = AccentSoftDark, gold = GoldDark, onAccent = Color(0xFF0F1320)),
)

val RastehBrand = BrandConfig(
    id = "rasteh",
    appName = "راسته",
    colors = RastehBrandColors,
    currency = "تومان",
    features = BrandFeatures(),
)

/** رجیستریِ برندها — انتخابِ برندِ فعال در زمانِ اجرا بر اساسِ شناسه (فلِیور/آرگومان). */
object BrandRegistry {
    val default: BrandConfig = RastehBrand
    private val all: List<BrandConfig> = listOf(RastehBrand)
    fun byId(id: String?): BrandConfig = all.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: default
}

/** برندِ فعال به‌صورتِ CompositionLocal تا هر Composable بتواند پرچم‌ها/نام را بخواند. */
val LocalBrand = staticCompositionLocalOf { BrandRegistry.default }

@Composable
fun ProvideBrand(brand: BrandConfig = BrandRegistry.default, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalBrand provides brand) { content() }
}
