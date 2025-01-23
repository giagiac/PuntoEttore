package it.puntoettore.fidelity

import com.mmk.kmpauth.google.GoogleAuthCredentials
import com.mmk.kmpauth.google.GoogleAuthProvider
import it.puntoettore.fidelity.custom.BuildConfig


object AppInitializer {
    fun onApplicationStart() {
        onApplicationStartPlatformSpecific()
        GoogleAuthProvider.create(credentials = GoogleAuthCredentials(serverId = BuildConfig.SERVER_ID))
    }
}