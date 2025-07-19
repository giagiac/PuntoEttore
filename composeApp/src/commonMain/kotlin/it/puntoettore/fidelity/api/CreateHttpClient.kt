package it.puntoettore.fidelity.api

import io.ktor.client.HttpClient
import it.puntoettore.fidelity.data.BookDatabase
import kotlinx.serialization.Serializable

expect fun createHttpClient(bookDatabase: BookDatabase): HttpClient

// Data class per la risposta di autenticazione
@Serializable
data class AuthResponse(
    var token_type: String, var expires_in: Int, var access_token: String, var refresh_token: String
)