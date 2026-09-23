package com.kazemieh.network.common

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import com.kazemieh.network.profile.dto.response.UserResponse
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SafeApiCallRawMockEngineTest {
    @Test
    fun `mock engine response is decoded into a typed API payload`() = runTest {
        val client = HttpClient(MockEngine {
            respond(
                content = "{\"id\":7,\"email\":\"person@example.invalid\",\"city\":\"Tehran\",\"postalCode\":12345,\"role\":\"CUSTOMER\",\"active\":true}",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        })

        val payload: UserResponse = safeApiCallRaw { client.get("/fixture") }

        assertEquals(7L, payload.id)
        assertEquals("CUSTOMER", payload.role)
        client.close()
    }
}
