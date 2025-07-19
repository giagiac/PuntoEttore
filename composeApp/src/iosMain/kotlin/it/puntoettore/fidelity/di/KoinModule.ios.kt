package it.puntoettore.fidelity.di

import it.puntoettore.fidelity.database.getDatabaseBuilder
import org.koin.dsl.module

actual val targetModule = module {
    single { getDatabaseBuilder() }
//    single { InsultCensorClient(httpClient = createHttpClient()) }
//    single { ApiDataClient(httpClient = createHttpClient()) }
}