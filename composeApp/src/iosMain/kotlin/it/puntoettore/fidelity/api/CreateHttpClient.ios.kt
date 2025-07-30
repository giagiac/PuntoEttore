package it.puntoettore.fidelity.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import it.puntoettore.fidelity.custom.BuildConfig
import it.puntoettore.fidelity.data.BookDatabase
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

//In shared/androidMain
actual fun createHttpClient(bookDatabase: BookDatabase): HttpClient = HttpClient(Darwin) {

    //Timeout plugin for timeouts
    install(HttpTimeout) {
        socketTimeoutMillis = 15_000
        requestTimeoutMillis = 15_000
    }
    //Logging plugin combined with kermit(KMP Logger library)
    if (BuildConfig.IS_DEBUG) {
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
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
}