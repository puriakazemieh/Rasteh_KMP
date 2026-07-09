package com.kazemieh.common

/**
 * نقش‌های کاربر (هم‌تراز با enum سمتِ سرور: CUSTOMER / VENDOR / ADMIN).
 * روی کلاینت نقش به‌صورتِ رشته حمل می‌شود، بنابراین این ثابت‌ها برای مقایسهٔ ایمن‌اند.
 */
object Roles {
    const val CUSTOMER = "CUSTOMER"
    const val VENDOR = "VENDOR"
    const val ADMIN = "ADMIN"
}
