package it.puntoettore.fidelity.di

import it.puntoettore.fidelity.api.ApiDataClient
import it.puntoettore.fidelity.api.ApiDataClientNextLogin
import it.puntoettore.fidelity.api.createHttpClient
import it.puntoettore.fidelity.api.createHttpClientNextLogin
import it.puntoettore.fidelity.database.getDatabaseBuilder
import org.koin.dsl.module

actual val targetModule = module {
    single { getDatabaseBuilder() }
    single { ApiDataClient(httpClient = createHttpClient(get())) }
    single { ApiDataClientNextLogin(httpClient = createHttpClientNextLogin(get())) }
}