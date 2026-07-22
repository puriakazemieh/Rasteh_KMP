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

private val VISUALS: Map<String, RastehVisual> = mapOf(
    "sofa" to RastehVisual(Resources.Icon.RastehSofa, Color(0xFFB07A4A)),
    "mobile" to RastehVisual(Resources.Icon.RastehMobile, Color(0xFF4E63C6)),
    "clothing" to RastehVisual(Resources.Icon.RastehClothing, Color(0xFFC65B93)),
    "gold" to RastehVisual(Resources.Icon.RastehGold, Color(0xFFC9A24B)),
    "appliances" to RastehVisual(Resources.Icon.RastehAppliances, Color(0xFF3F97A6)),
    "bag" to RastehVisual(Resources.Icon.RastehBag, Color(0xFFBB6A4A)),
    "cosmetics" to RastehVisual(Resources.Icon.RastehCosmetics, Color(0xFFCB5A97)),
    "book" to RastehVisual(Resources.Icon.RastehBook, Color(0xFF4C9B72)),
    "toy" to RastehVisual(Resources.Icon.RastehToy, Color(0xFFCB963F)),
    "all" to RastehVisual(Resources.Icon.RastehAll, Color(0xFF8B46DF)),
)

private val FALLBACK = RastehVisual(Resources.Icon.RastehAll, Color(0xFF8B46DF))

fun rastehVisual(iconKey: String?): RastehVisual = VISUALS[iconKey] ?: FALLBACK
