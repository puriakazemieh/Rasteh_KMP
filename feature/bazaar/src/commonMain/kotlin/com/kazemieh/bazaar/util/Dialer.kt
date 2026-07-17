package com.kazemieh.bazaar.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalUriHandler

/**
 * شماره‌گیرِ سیستم را برای شمارهٔ داده‌شده باز می‌کند (طرحِ `tel:`).
 *
 * روی همهٔ پلتفرم‌ها از `UriHandler`ِ Compose استفاده می‌کنیم؛ روی موبایل شماره‌گیرِ گوشی
 * و روی دسکتاپ/وب هندلرِ پیش‌فرضِ `tel:` باز می‌شود. اگر پلتفرمی از این طرح پشتیبانی نکند،
 * خطا بی‌صدا نادیده گرفته می‌شود تا اپ کرش نکند.
 */
@Composable
fun rememberPhoneDialer(): (String) -> Unit {
    val uriHandler = LocalUriHandler.current
    return remember(uriHandler) {
        { phone ->
            val digits = phone.filter { it.isDigit() || it == '+' }
            if (digits.isNotBlank()) {
                runCatching { uriHandler.openUri("tel:$digits") }
            }
        }
    }
}
