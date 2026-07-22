package com.kazemieh.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp

/**
 * فونتِ فارسی (وزیرمتن/IRANYekan).
 *
 * روی وب، `Font(Res.font.typeface_fa)` به‌درستی رندر نمی‌شود؛ برای همین بارگذاریِ فونت را
 * per-platform انجام می‌دهیم: نسخهٔ غیرِوب از فونتِ منبع استفاده می‌کند و نسخهٔ وب فونت را از
 * روی بایت‌ها می‌سازد. جزئیات در `appFontFamily()` (expect/actual).
 */
@Composable
fun AppFont(): FontFamily = appFontFamily()

@Composable
expect fun appFontFamily(): FontFamily

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
