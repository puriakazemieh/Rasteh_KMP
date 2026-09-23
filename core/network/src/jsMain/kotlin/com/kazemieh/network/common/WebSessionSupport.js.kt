package com.kazemieh.network.common

import kotlinx.browser.document

actual fun usesWebCookieSession(): Boolean = true

actual fun webCsrfToken(): String? = document.cookie
    .split("; ")
    .firstOrNull { it.startsWith("XSRF-TOKEN=") }
    ?.substringAfter("XSRF-TOKEN=")
