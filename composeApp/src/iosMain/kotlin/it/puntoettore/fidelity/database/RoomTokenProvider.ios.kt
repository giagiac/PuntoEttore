package it.puntoettore.fidelity.database

import it.puntoettore.fidelity.data.BookDatabase
import it.puntoettore.fidelity.data.TokenProvider
import it.puntoettore.fidelity.di.Pippo

actual fun getMieiDati(bookDatabase: BookDatabase):Pippo {
    return Pippo("HELLO")
}