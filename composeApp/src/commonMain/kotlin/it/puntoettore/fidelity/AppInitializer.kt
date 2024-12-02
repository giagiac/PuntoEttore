package it.puntoettore.fidelity

import com.mmk.kmpauth.google.GoogleAuthCredentials
import com.mmk.kmpauth.google.GoogleAuthProvider


object AppInitializer {
    fun onApplicationStart() {
        onApplicationStartPlatformSpecific()
        GoogleAuthProvider.create(credentials = GoogleAuthCredentials(serverId = "834229780666-i4lcikfn7dh54b3khqapppbg1hg4igfr.apps.googleusercontent.com"))
    }
}