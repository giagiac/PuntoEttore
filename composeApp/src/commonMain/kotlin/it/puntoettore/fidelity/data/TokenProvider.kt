package it.puntoettore.fidelity.data

interface TokenProvider {
    suspend fun getToken(): String?
}