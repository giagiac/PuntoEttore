package it.puntoettore.fidelity

import androidx.compose.ui.window.ComposeUIViewController
import it.puntoettore.fidelity.di.initializeKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        initializeKoin()
    }
) { App() }


