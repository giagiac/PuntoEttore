package it.puntoettore.fidelity.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

//In shared/androidMain
@OptIn(ExperimentalSerializationApi::class)
actual fun createHttpClient(): HttpClient = HttpClient(OkHttp) {
    var accessToken: String = "ACCESS_NOT_DEFINED"
    var refreshToken: String = "REFRESH_NOT_DEFINED"

    //Timeout plugin for timeouts
    install(HttpTimeout) {
        socketTimeoutMillis = 60_000
        requestTimeoutMillis = 60_000
    }
    //Logging plugin combined with kermit(KMP Logger library)
    install(Logging) {
        logger = Logger.DEFAULT
        level = LogLevel.ALL
    }
    //We can configure the BASE_URL and also
    //the deafult headers by defaultRequest builder
    defaultRequest {
        header("Content-Type", "application/json")
        header("Authorization", "Bearer ${"TEST"}")
        // url("https://api.openai.com/v1/")
    }
    //ContentNegotiation plugin for negotiationing media types between the client and server
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
            explicitNulls = false
        })
    }
    // Aggiungi l'autenticazione con token e refresh token
    install(Auth)
    {
        bearer {
            // Carica il token iniziale
            loadTokens {
                BearerTokens(
                    accessToken = accessToken,
                    refreshToken = refreshToken
                )
            }

            // Definisci come aggiornare i token
            refreshTokens {
                // Esegui una richiesta al tuo endpoint di autenticazione
                // usando il refresh token.
                val response = client.post("https://app.erroridiconiazione.com/auth/refresh") {
                    setBody(
                        // Invia il refresh token nel body della richiesta
                        RefreshRequest(refreshToken = refreshToken)
                    )
                }

                // Estrai i nuovi token dalla risposta
                val tokens = response.body<AuthResponse>()

                // Aggiorna i token nella struttura Auth
                // accessToken = tokens.accessToken
                // refreshToken = tokens.refreshToken

                // Restituisci i nuovi token a Ktor
                BearerTokens(
                    accessToken = tokens.accessToken,
                    refreshToken = tokens.refreshToken
                )
            }

            // Configura l'header di autorizzazione
            sendWithoutRequest { request ->
                !request.url.toString().contains("https://app.erroridiconiazione.com/auth/refresh")
            }
        }
    }
}

// Data class per la richiesta di refresh token
@Serializable
data class RefreshRequest(val refreshToken: String)

// Data class per la risposta di autenticazione
@Serializable
data class AuthResponse(val accessToken: String, val refreshToken: String)
