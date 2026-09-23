package com.kazemieh.network.common

/**
 * Browser builds use the server-owned web session. Native builds continue to
 * use the mobile API contract and never depend on a browser cookie.
 */
expect fun usesWebCookieSession(): Boolean

/**
 * Returns the double-submit CSRF value when the current platform has one.
 * It is deliberately absent on native platforms.
 */
expect fun webCsrfToken(): String?
