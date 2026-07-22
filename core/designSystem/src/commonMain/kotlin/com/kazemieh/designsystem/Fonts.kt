package com.kazemieh.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.preloadFont


/**
 * فونتِ فارسی (وزیرمتن/IRANYekan).
 *
 * روی وب (Kotlin/JS، رندرِ canvas با Skiko) فونتِ منبع به‌صورتِ async لود می‌شود؛ اگر
 * مستقیماً `Font(Res.font.typeface_fa)` بسازیم، اولین ترکیب فونت را ندارد و چون بازترکیبی
 * برای فونتِ منبع به‌درستی تریگر نمی‌شود، متن به‌صورتِ مربع/tofu می‌ماند. `preloadFont`
 * فونت را پیش‌بارگذاری می‌کند و به‌محضِ آماده‌شدن State را به‌روز می‌کند تا کلِ اپ با فونتِ
 * درست بازرندر شود (روی همهٔ پلتفرم‌ها یکسان کار می‌کند).
 */
@Composable
fun AppFont(): FontFamily {
    val font by preloadFont(Res.font.typeface_fa)
    return font?.let { FontFamily(it) } ?: FontFamily.Default
}

object FontSize {
    val EXTRA_SMALL = 10.sp
    val SMALL = 12.sp
    val REGULAR = 14.sp
    val EXTRA_REGULAR = 16.sp
    val MEDIUM = 18.sp
    val EXTRA_MEDIUM = 20.sp
    val LARGE = 30.sp
    val EXTRA_LARGE = 40.sp
}
