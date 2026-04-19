package com.kaelmoreno.compose.composemultiplatformbase.data.network

import com.kaelmoreno.compose.composemultiplatformbase.Logger
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object NetworkClient {

    fun createHttpClient(
        loadTokens: (suspend () -> BearerTokens?)? = null,
        refreshTokens: (suspend RefreshTokensParams.() -> BearerTokens?)? = null
    ): HttpClient = HttpClient {

        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                prettyPrint = false
            })
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 60000
            connectTimeoutMillis = 60000
            socketTimeoutMillis = 60000
        }

        install(Logging) {
            logger = object : io.ktor.client.plugins.logging.Logger {
                override fun log(message: String) {
                    com.kaelmoreno.compose.composemultiplatformbase.Logger.d(message, "Ktor")
                }
            }
            level = LogLevel.ALL
        }

        defaultRequest {
            contentType(ContentType.Application.Json)
        }

        if (loadTokens != null) {
            install(Auth) {
                bearer {
                    loadTokens(loadTokens)
                    if (refreshTokens != null) {
                        refreshTokens(refreshTokens)
                    }
                }
            }
        }

        expectSuccess = false
    }

    // Default client without auth for backward compatibility
    val httpClient = createHttpClient()
}
