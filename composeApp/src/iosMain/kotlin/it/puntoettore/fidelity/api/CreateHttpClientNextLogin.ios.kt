package it.puntoettore.fidelity.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.headers
import io.ktor.serialization.kotlinx.json.json
import it.puntoettore.fidelity.custom.BuildConfig
import it.puntoettore.fidelity.data.BookDatabase
import it.puntoettore.fidelity.domain.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

//In shared/androidMain
@OptIn(ExperimentalSerializationApi::class)
actual fun createHttpClientNextLogin(bookDatabase: BookDatabase): HttpClient = HttpClient(Darwin) {

    // Scope per operazioni asincrone (iOS/Native)
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    lateinit var accessToken: String
    lateinit var refreshToken: String

    lateinit var user: User

    scope.launch {
        bookDatabase.appSettingsDao().getAppSettings().collect {

            accessToken = ""
            refreshToken = ""

            val idUser = it?._idUser
            idUser?.let { idUserNotNull ->
                val _user = bookDatabase.userDao().getUserById(idUserNotNull).first()

                _user?.let { userNotNull ->
                    user = userNotNull
                    accessToken = userNotNull.accessToken.toString()
                    refreshToken = userNotNull.refreshToken.toString()
                }
            }
        }
    }

    val CustomHeadersPlugin = createClientPlugin("CustomHeadersPlugin") {
        // L'hook onRequest è il posto giusto per modificare la richiesta prima che venga inviata
        onRequest { request, _ ->
            request.headers.apply {
                user?.let {
                    append("TOKEN_NOTIFICATION_PUSH", it.notifierToken)
                }
            }
        }
    }

    install(CustomHeadersPlugin)

    //Timeout plugin for timeouts
    install(HttpTimeout) {
        socketTimeoutMillis = 15_000
        requestTimeoutMillis = 15_000
    }
    //Logging plugin
    if (BuildConfig.IS_DEBUG) {
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }
    }
    install("RefreshToken") {
        sendPipeline.intercept(io.ktor.client.request.HttpSendPipeline.Before) {
            println("Bearer $accessToken")
            headers {
                append("custom-refresh-token", "Bearer $refreshToken")
            }
            proceed()
        }
    }
    //We can configure the BASE_URL and also
    //the deafult headers by defaultRequest builder
    defaultRequest {
        header("Content-Type", "application/json")
        header("APP_VERSION", BuildConfig.APP_VERSION)
        header("BUILD_TIME", BuildConfig.BUILD_TIME)
        header("FEATURE_ENABLED", BuildConfig.FEATURE_ENABLED)
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
    install(io.ktor.client.plugins.auth.Auth) {
        bearer {
            // Carica il token iniziale
            loadTokens {
                io.ktor.client.plugins.auth.providers.BearerTokens(
                    accessToken = accessToken,
                    refreshToken = "Bearer $refreshToken"
                )
            }
            // Definisci come aggiornare i token
            refreshTokens {
                // Esegui una richiesta al tuo endpoint di autenticazione usando il refresh token.
                val response =
                    client.get("${BuildConfig.END_POINT}/index.php?entryPoint=getRefresh") {
                        header("refresh-token", refreshToken)
                    }
                // Estrai i nuovi token dalla risposta
                val tokens = response.body<AuthResponse>()
                scope.launch {
                    user?.let {
                        it.refreshToken = tokens.refresh_token
                        it.accessToken = tokens.access_token
                        bookDatabase.userDao().updateUser(user = it)
                    }
                    // println("CreateHttpClientNextLogin.ios.kt: Token refreshed")
                }
                io.ktor.client.plugins.auth.providers.BearerTokens(
                    accessToken = tokens.access_token,
                    refreshToken = "Bearer ${tokens.refresh_token}"
                )
            }
            // Configura l'header di autorizzazione
            sendWithoutRequest { request ->
                !request.url.toString()
                    .contains("${BuildConfig.END_POINT}/index.php?entryPoint=getAccess")
            }
        }
    }
}