package dev.watchstore.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/** See `defaultApiBaseUrl` in the common declaration. */
private const val DEV_API_BASE_URL = "http://10.0.2.2:8787"

actual fun defaultApiBaseUrl(): String = DEV_API_BASE_URL

actual fun createHttpClient(): HttpClient = HttpClient(OkHttp) {
    expectSuccess = false

    // Short timeouts: the catalogue is a small payload, and a slow network
    // should fall back to the seed quickly rather than leave the UI waiting.
    install(HttpTimeout) {
        requestTimeoutMillis = 4_000
        connectTimeoutMillis = 2_000
        socketTimeoutMillis = 4_000
    }

    install(ContentNegotiation) {
        json(
            Json {
                // Tolerate unknown keys so a server can add fields without
                // breaking this build.
                ignoreUnknownKeys = true
                explicitNulls = false
            },
        )
    }
}