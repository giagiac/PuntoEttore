package it.puntoettore.fidelity.di

//fun koinModuleNetwork() = HttpClient {
//    install(ContentNegotiation) {
//        json(json = Json { ignoreUnknownKeys = true }, contentType = ContentType.Any)
//    }
//}

import io.ktor.client.HttpClient

expect val client: HttpClient