package com.kazemieh.bazaar.util

import androidx.compose.ui.graphics.Color

/**
 * نگاشتِ کلیدِ آیکونِ راسته (که سرور در `iconKey` می‌فرستد) به یک اموجی و یک رنگِ لهجه.
 *
 * سرور رنگ را به‌صورتِ رشتهٔ `oklch(...)` می‌فرستد؛ چون تبدیلِ زندهٔ oklch→sRGB روی کلاینت
 * سنگین و خطاخیز است، از یک پالتِ ثابتِ منطبق بر بستهٔ طراحیِ v2 استفاده می‌کنیم و رشتهٔ
 * oklch فقط به‌عنوانِ داده نگه‌داری می‌شود. کلیدهای ناشناخته به رنگِ برند/اموجیِ عمومی می‌افتند.
 */
data class RastehVisual(val emoji: String, val color: Color)

private val VISUALS: Map<String, RastehVisual> = mapOf(
    "sofa" to RastehVisual("🛋️", Color(0xFFB07A4A)),
    "mobile" to RastehVisual("📱", Color(0xFF4E63C6)),
    "clothing" to RastehVisual("👕", Color(0xFFC65B93)),
    "gold" to RastehVisual("💍", Color(0xFFC9A24B)),
    "appliances" to RastehVisual("🧺", Color(0xFF3F97A6)),
    "bag" to RastehVisual("👜", Color(0xFFBB6A4A)),
    "cosmetics" to RastehVisual("💄", Color(0xFFCB5A97)),
    "book" to RastehVisual("📚", Color(0xFF4C9B72)),
    "toy" to RastehVisual("🧸", Color(0xFFCB963F)),
    "all" to RastehVisual("🏬", Color(0xFF8B46DF)),
)

private val FALLBACK = RastehVisual("🏬", Color(0xFF8B46DF))

fun rastehVisual(iconKey: String?): RastehVisual = VISUALS[iconKey] ?: FALLBACK
