package com.kazemieh.network.common

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.js.Js
import io.ktor.client.engine.js.JsClientEngineConfig

actual fun httpClientEngine(): HttpClientEngineFactory<*> = object : HttpClientEngineFactory<JsClientEngineConfig> {
    override fun create(block: JsClientEngineConfig.() -> Unit): HttpClientEngine = Js.create {
        // Browser fetch omits cookies across origins unless this is set explicitly.
        // The web BFF relies on HttpOnly cookies, never JavaScript-held JWTs.
        configureRequest {
            credentials = "include"
        }
        block()
    }
}
