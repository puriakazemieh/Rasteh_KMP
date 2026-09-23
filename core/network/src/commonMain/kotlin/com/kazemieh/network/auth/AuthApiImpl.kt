package com.kazemieh.network.auth

import com.kazemieh.network.common.PageResponse
import com.kazemieh.network.common.safeApiCallRaw
import com.kazemieh.network.common.usesWebCookieSession
import com.kazemieh.network.auth.dto.request.LoginRequest
import com.kazemieh.network.auth.dto.request.LogoutRequest
import com.kazemieh.network.auth.dto.request.RegisterRequest
import com.kazemieh.network.auth.dto.request.ResetPasswordRequest
import com.kazemieh.network.auth.dto.request.SendLoginOtpRequest
import com.kazemieh.network.auth.dto.request.LoginWithOtpRequest
import com.kazemieh.network.auth.dto.request.ForgotPasswordRequest
import com.kazemieh.network.auth.dto.request.ResetPasswordWithOtpRequest
import com.kazemieh.network.auth.dto.response.AuthResponse
import com.kazemieh.network.profile.dto.response.UserResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse

class AuthApiImpl(
    private val client: HttpClient
) : AuthApi {

    override suspend fun login(
        request: LoginRequest
    ): AuthResponse = if (usesWebCookieSession()) {
        webSessionAuth { client.post("web/auth/login") { setBody(request) } }
    } else safeApiCallRaw {
        client.post("api/auth/login") { setBody(request) }
    }

    override suspend fun register(
        request: RegisterRequest
    ): AuthResponse = if (usesWebCookieSession()) {
        webSessionAuth { client.post("web/auth/register") { setBody(request) } }
    } else safeApiCallRaw {
        client.post("api/auth/register") { setBody(request) }
    }

    override suspend fun forgotPassword(
        request: ForgotPasswordRequest
    ) = safeApiCallRaw<Unit> {
        client.post("api/auth/forgot-password") { setBody(request) }
    }

    override suspend fun resetPassword(
        request: ResetPasswordRequest
    ) = safeApiCallRaw<Unit> {
        client.post("api/auth/reset-password") { setBody(request) }
    }

    override suspend fun sendLoginOtp(request: SendLoginOtpRequest) = safeApiCallRaw<Unit> {
        client.post("api/auth/send-login-otp") { setBody(request) }
    }

    override suspend fun loginWithOtp(request: LoginWithOtpRequest): AuthResponse = if (usesWebCookieSession()) {
        webSessionAuth { client.post("web/auth/login-with-otp") { setBody(request) } }
    } else safeApiCallRaw {
        client.post("api/auth/login-with-otp") { setBody(request) }
    }

    override suspend fun webSession(): AuthResponse {
        check(usesWebCookieSession()) { "Web session endpoint is browser-only" }
        return webSessionAuth { client.get("web/auth/session") }
    }

    override suspend fun resetPasswordWithOtp(request: ResetPasswordWithOtpRequest) = safeApiCallRaw<Unit> {
        client.post("api/auth/reset-password-with-otp") { setBody(request) }
    }

    override suspend fun logout(request: LogoutRequest) = safeApiCallRaw<Unit> {
        if (usesWebCookieSession()) {
            client.post("web/auth/logout")
        } else {
            client.post("api/auth/logout") { setBody(request) }
        }
    }

    private suspend fun webSessionAuth(request: suspend () -> HttpResponse): AuthResponse {
        val user = safeApiCallRaw<UserResponse> { request() }
        // This response initializes the non-HttpOnly XSRF cookie. The next
        // unsafe request carries its value in a header via DefaultRequest.
        safeApiCallRaw<Unit> { client.get("web/auth/csrf") }
        return AuthResponse(accessToken = "", refreshToken = "", user = user)
    }
}
