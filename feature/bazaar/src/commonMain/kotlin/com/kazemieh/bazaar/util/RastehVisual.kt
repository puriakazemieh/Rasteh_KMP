package com.kazemieh.bazaar.util

import androidx.compose.ui.graphics.Color
import com.kazemieh.designsystem.Resources
import org.jetbrains.compose.resources.DrawableResource

/**
 * نگاشتِ کلیدِ آیکونِ راسته (که سرور در `iconKey` می‌فرستد) به یک آیکونِ برداری و یک رنگِ لهجه.
 *
 * قبلاً از اموجی استفاده می‌شد، اما روی وب (بومِ Skiko) اموجی رندر نمی‌شود و tofu می‌دهد؛
 * برای همین از آیکون‌های برداری (SVG) استفاده می‌کنیم که با رنگِ اختصاصیِ راسته tint می‌شوند
 * — منطبق بر بستهٔ طراحی («SVG با رنگِ اختصاصیِ راسته»).
 *
 * سرور رنگ را به‌صورتِ رشتهٔ `oklch(...)` می‌فرستد؛ چون تبدیلِ زندهٔ oklch→sRGB روی کلاینت
 * سنگین و خطاخیز است، از یک پالتِ ثابتِ منطبق بر بستهٔ طراحی استفاده می‌کنیم.
 */
data class RastehVisual(val icon: DrawableResource, val color: Color)

// رنگ‌ها دقیقاً از مقادیرِ oklchِ بستهٔ طراحی (README §11) به sRGB تبدیل شده‌اند.
private val VISUALS: Map<String, RastehVisual> = mapOf(
    "sofa" to RastehVisual(Resources.Icon.RastehSofa, Color(0xFFAE5528)),        // oklch(0.55 0.13 45)
    "mobile" to RastehVisual(Resources.Icon.RastehMobile, Color(0xFF2F60B2)),    // oklch(0.5 0.14 260)
    "clothing" to RastehVisual(Resources.Icon.RastehClothing, Color(0xFFAF467E)),// oklch(0.55 0.15 350)
    "gold" to RastehVisual(Resources.Icon.RastehGold, Color(0xFFAA7E00)),        // oklch(0.62 0.13 85)
    "appliances" to RastehVisual(Resources.Icon.RastehAppliances, Color(0xFF00858D)), // oklch(0.55 0.12 200)
    "bag" to RastehVisual(Resources.Icon.RastehBag, Color(0xFFA04034)),          // oklch(0.5 0.13 30)
    "cosmetics" to RastehVisual(Resources.Icon.RastehCosmetics, Color(0xFFB94F87)),   // oklch(0.58 0.15 350)
    "book" to RastehVisual(Resources.Icon.RastehBook, Color(0xFF3B834E)),        // oklch(0.55 0.11 150)
    "toy" to RastehVisual(Resources.Icon.RastehToy, Color(0xFFBB6802)),          // oklch(0.6 0.14 60)
    "all" to RastehVisual(Resources.Icon.RastehAll, Color(0xFF61626F)),          // oklch(0.5 0.02 280) — خنثی
)

private val FALLBACK = RastehVisual(Resources.Icon.RastehAll, Color(0xFF61626F))

fun rastehVisual(iconKey: String?): RastehVisual = VISUALS[iconKey] ?: FALLBACK
