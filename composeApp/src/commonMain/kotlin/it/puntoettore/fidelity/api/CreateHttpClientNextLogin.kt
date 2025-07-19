package it.puntoettore.fidelity.api

import io.ktor.client.HttpClient
import it.puntoettore.fidelity.data.BookDatabase
import it.puntoettore.fidelity.data.TokenProvider
import it.puntoettore.fidelity.di.Pippo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

expect fun createHttpClientNextLogin(bookDatabase: BookDatabase): HttpClient
