package it.puntoettore.fidelity.api

import io.ktor.client.HttpClient
import it.puntoettore.fidelity.data.BookDatabase

expect fun createHttpClientNextLogin(bookDatabase: BookDatabase): HttpClient
