package com.kazemieh.data.local

import com.kazemieh.network.common.*
import com.kazemieh.common.*

import com.russhwolf.settings.Settings

class TokenManager(private val settings: Settings) : TokenProvider {

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
    }

    private var onTokenExpiredListener: (() -> Unit)? = null
    private var accessToken: String? = null
    private var refreshToken: String? = null

    init {
        // Legacy versions persisted bearer credentials in general settings.
        // There is no safe portable migration target yet, so revoke the local copy
        // rather than restoring a plaintext credential after an app restart.
        settings.remove(KEY_ACCESS_TOKEN)
        settings.remove(KEY_REFRESH_TOKEN)
    }

    override fun saveTokens(accessToken: String, refreshToken: String) {
        this.accessToken = accessToken.takeIf { it.isNotBlank() }
        this.refreshToken = refreshToken.takeIf { it.isNotBlank() }
    }

    override fun getAccessToken(): String? {
        return accessToken
    }

    override fun getRefreshToken(): String? {
        return refreshToken
    }

    override fun clearTokens() {
        accessToken = null
        refreshToken = null
        settings.remove(KEY_ACCESS_TOKEN)
        settings.remove(KEY_REFRESH_TOKEN)
    }

    override fun setOnTokenExpiredListener(listener: () -> Unit) {
        onTokenExpiredListener = listener
    }

    override fun notifyTokenExpired() {
        onTokenExpiredListener?.invoke()
    }

    fun hasValidToken(): Boolean {
        return !getAccessToken().isNullOrEmpty()
    }
}
