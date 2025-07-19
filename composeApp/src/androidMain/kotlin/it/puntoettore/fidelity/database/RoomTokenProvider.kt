package it.puntoettore.fidelity.database

import android.util.Log
import it.puntoettore.fidelity.data.BookDatabase
import it.puntoettore.fidelity.di.Pippo
import it.puntoettore.fidelity.domain.AppSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

actual fun getMieiDati(bookDatabase: BookDatabase): Pippo {
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    lateinit var outvar: String
    var idUser: Int? = null

    scope.launch {
        bookDatabase.appSettingsDao().getAppSettings().collect { p ->
            outvar = p?.darkMode.toString()
            idUser = p?._idUser
            Log.e("TEST in COLL", p?.darkMode.toString())
        }
//        delay(5000) // Attendi 5 secondi
//
//        val dati = bookDatabase.appSettingsDao().getAppSettings().first()
//        Log.e("TEST", dati?.darkMode.toString())
//        // Qui potresti salvare il risultato in un database,
//        // aggiornare uno StateFlow, ecc.
    }

    scope.launch {
        delay(15000)
        idUser?.let {
            bookDatabase.appSettingsDao()
                .updateAppSettings(AppSettings(_idUser = it, darkMode = true))
        }
        Log.e("TEST 15", outvar + " " + idUser)
    }

    scope.launch {

        delay(10000) // Attendi 5 secondi
        Log.e("TEST 10", outvar + " " + idUser)
    }

    return Pippo("Helllllooooo")
}