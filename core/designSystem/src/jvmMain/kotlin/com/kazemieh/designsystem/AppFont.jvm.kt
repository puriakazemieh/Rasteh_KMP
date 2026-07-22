package com.kazemieh.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import org.jetbrains.compose.resources.Font

// فونتِ منبع روی این پلتفرم‌ها درست رندر می‌شود.
@Composable
actual fun appFontFamily(): FontFamily = FontFamily(Font(Res.font.typeface_fa))
