package it.puntoettore.fidelity

import android.app.Application
import it.puntoettore.fidelity.di.initializeKoin
import org.koin.android.ext.koin.androidContext

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppInitializer.onApplicationStart()
        initializeKoin {
            androidContext(this@MainApplication)
        }
    }
}