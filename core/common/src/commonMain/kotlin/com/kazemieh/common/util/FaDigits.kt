package com.kazemieh.common.util

/**
 * تبدیلِ ارقامِ لاتین (۰۹) به ارقامِ فارسی (۰۹) — معادلِ `faDigits` در بستهٔ طراحی.
 *
 * در سراسرِ اپ برای نمایشِ اعداد/قیمت/تایمر استفاده می‌شود (RTL فارسی، طبقِ توکن‌های دیزاین).
 * جداکنندهٔ هزارگان (`,` یا `٬`) و بقیهٔ کاراکترها بدون تغییر عبور می‌کنند.
 */
private val FA_DIGITS = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')

fun String.toFaDigits(): String = buildString(length) {
    for (ch in this@toFaDigits) {
        append(if (ch in '0'..'9') FA_DIGITS[ch - '0'] else ch)
    }
}

fun Int.toFaDigits(): String = toString().toFaDigits()

fun Long.toFaDigits(): String = toString().toFaDigits()

/** جداسازیِ هزارگان + تبدیل به ارقامِ فارسی (مثال: 485000 → «۴۸۵٬۰۰۰»). */
fun Long.toFaPrice(groupSeparator: Char = '٬'): String {
    val negative = this < 0
    val digits = kotlin.math.abs(this).toString()
    val grouped = buildString {
        val firstGroup = digits.length % 3
        digits.forEachIndexed { i, c ->
            if (i != 0 && (i - firstGroup) % 3 == 0) append(groupSeparator)
            append(c)
        }
    }
    return (if (negative) "-$grouped" else grouped).toFaDigits()
}
