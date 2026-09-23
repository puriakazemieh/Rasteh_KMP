package com.kazemieh.data.auth.repository

import com.kazemieh.common.AppResult
import com.kazemieh.data.auth.source.AuthDataSource
import com.kazemieh.data.local.ProfileLocalDataSource
import com.kazemieh.data.local.TokenManager
import com.kazemieh.domain.auth.Auth
import com.kazemieh.domain.profile.Profile
import com.russhwolf.settings.PropertiesSettings
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import java.util.Properties
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class AuthRepositorySignOutTest {
    @Test
    fun `tokens are not restored from general settings after restart`() {
        val settings = PropertiesSettings(Properties())
        TokenManager(settings).saveTokens(accessToken = "test-access", refreshToken = "test-refresh")

        val restartedManager = TokenManager(settings)

        assertFalse(restartedManager.hasValidToken())
        assertNull(restartedManager.getRefreshToken())
    }

    @Test
    fun `sign out clears tokens and identity-bound profile cache`() = runTest {
        val settings = PropertiesSettings(Properties())
        val tokenManager = TokenManager(settings)
        val profileStore = ProfileLocalDataSource(settings)
        tokenManager.saveTokens(accessToken = "test-access", refreshToken = "test-refresh")
        profileStore.saveProfile(
            Profile(
                id = 42,
                email = null,
                city = null,
                role = "USER",
                postalCode = null,
            )
        )

        val authDataSource = FakeAuthDataSource()
        val repository = AuthRepositoryImpl(authDataSource, tokenManager, profileStore)

        repository.signOut()

        assertNull(tokenManager.getAccessToken())
        assertNull(tokenManager.getRefreshToken())
        assertNull(profileStore.observeProfile().first())
        assertEquals("test-refresh", authDataSource.loggedOutRefreshToken)
    }

    private class FakeAuthDataSource : AuthDataSource {
        var loggedOutRefreshToken: String? = null
        override suspend fun login(email: String, password: String): AppResult<Auth> = error("unused")
        override suspend fun register(email: String?, mobile: String?, password: String): AppResult<Auth> = error("unused")
        override suspend fun forgotPassword(email: String?, mobile: String?): AppResult<Unit> = error("unused")
        override suspend fun resetPassword(token: String, newPassword: String): AppResult<Unit> = error("unused")
        override suspend fun sendLoginOtp(mobile: String): AppResult<Unit> = error("unused")
        override suspend fun loginWithOtp(mobile: String, otpCode: String): AppResult<Auth> = error("unused")
        override suspend fun resetPasswordWithOtp(mobile: String, otpCode: String, newPassword: String): AppResult<Unit> = error("unused")
        override suspend fun logout(refreshToken: String): AppResult<Unit> {
            loggedOutRefreshToken = refreshToken
            return AppResult.Success(Unit)
        }
    }
}
