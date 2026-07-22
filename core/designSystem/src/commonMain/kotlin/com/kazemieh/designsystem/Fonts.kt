package com.kazemieh.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.ExperimentalResourceApi


/**
 * فونتِ فارسی (وزیرمتن/IRANYekan).
 *
 * روی وب (Kotlin/JS، رندرِ canvas با Skiko) ساختِ مستقیمِ `Font(Res.font.typeface_fa)`
 * به‌درستی پس از بارگذاریِ async بازرندر نمی‌شود و متن به‌صورتِ مربع/tofu می‌ماند. برای همین
 * بایت‌های فونت را با `Res.readBytes` می‌خوانیم و FontFamily را از روی بایت‌ها می‌سازیم؛
 * به‌محضِ آماده‌شدن State به‌روز و کلِ اپ با فونتِ درست بازرندر می‌شود (روی همهٔ پلتفرم‌ها).
 */
@OptIn(ExperimentalResourceApi::class)
@Composable
fun AppFont(): FontFamily {
    var family by remember { mutableStateOf<FontFamily?>(null) }
    LaunchedEffect(Unit) {
        family = runCatching {
            FontFamily(Font("typeface_fa", Res.readBytes("font/typeface_fa.ttf")))
        }.getOrNull()
    }
    return family ?: FontFamily.Default
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
