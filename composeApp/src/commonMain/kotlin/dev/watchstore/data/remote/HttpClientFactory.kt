package dev.watchstore.data.remote

import io.ktor.client.HttpClient

/**
 * Platform-tuned HTTP client.
 *
 * Only the engine differs, so the shared repository never sees an engine type.
 * Android uses OkHttp; add an iOS actual with the Darwin engine when the target
 * is enabled.
 */
expect fun createHttpClient(): HttpClient

/**
 * Where the catalogue API lives.
 *
 * Android emulators reach the host machine on 10.2.2.2 — actually
 * `10.0.2.2`; a physical device needs the LAN IP instead.
 */
expect fun defaultApiBaseUrl(): String