package com.kazemieh.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.platform.Font
import org.jetbrains.compose.resources.ExperimentalResourceApi

/**
 * روی وب فونتِ منبع async است و به‌درستی رندر نمی‌شود؛ بایت‌ها را می‌خوانیم و فونت را از روی
 * بایت (Skiko `Font(identity, data)`) می‌سازیم تا به‌محضِ آماده‌شدن، اپ با وزیرمتن رندر شود.
 */
@OptIn(ExperimentalResourceApi::class)
@Composable
actual fun appFontFamily(): FontFamily {
    var family by remember { mutableStateOf<FontFamily?>(null) }
    LaunchedEffect(Unit) {
        family = runCatching {
            FontFamily(Font("typeface_fa", Res.readBytes("font/typeface_fa.ttf")))
        }.getOrNull()
    }
    return family ?: FontFamily.Default
}
