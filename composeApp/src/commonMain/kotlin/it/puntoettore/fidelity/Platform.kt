package it.puntoettore.fidelity

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform