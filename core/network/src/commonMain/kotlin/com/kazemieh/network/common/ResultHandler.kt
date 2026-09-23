package com.kazemieh.network.common

import com.kazemieh.common.AppResult
import com.kazemieh.common.Res
import com.kazemieh.common.*
import com.kazemieh.common.toUserMessage
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

val json = Json {
    ignoreUnknownKeys = true
    isLenient = true
}

suspend fun <T> safeApiCall(apiCall: suspend () -> T): AppResult<T> = try {
    AppResult.Success(apiCall())
} catch (e: ApiException) {
    AppResult.Error(message = e.messageText, code = e.code)
} catch (e: CancellationException) {
    throw e
} catch (_: Exception) {
    AppResult.Error(message = Res.string.unknownError, code = 0)
}

suspend inline fun <reified T> safeApiCallRaw(
    crossinline request: suspend () -> HttpResponse,
): T = try {
    val response = request()
    val bodyText = try {
        response.bodyAsText()
    } catch (e: CancellationException) {
        throw e
    } catch (_: Exception) {
        ""
    }

    if (response.status.isSuccess()) {
        when (T::class) {
            Unit::class -> Unit as T
            String::class -> try {
                json.decodeFromString<T>(bodyText)
            } catch (_: Exception) {
                bodyText as T
            }
            else -> try {
                json.decodeFromString<T>(bodyText)
            } catch (_: SerializationException) {
                throw ApiException(messageText = Res.string.errorParsingData, code = 0)
            }
        }
    } else {
        val apiError = try {
            json.decodeFromString<ApiError>(bodyText)
        } catch (_: Exception) {
            ApiError(
                message = "Server error",
                status = response.status.value.toString(),
                code = response.status.value,
                errorCode = "UNKNOWN_ERROR",
                path = null,
                timestamp = null,
            )
        }
        throw ApiException(
            messageText = apiError.errorCode.toUserMessage(),
            code = apiError.code,
        )
    }
} catch (e: ApiException) {
    throw e
} catch (e: CancellationException) {
    throw e
} catch (_: Exception) {
    throw ApiException(messageText = Res.string.unknownError, code = 0)
}
