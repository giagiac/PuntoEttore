package it.puntoettore.fidelity

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import it.puntoettore.fidelity.navigation.Screen
import it.puntoettore.fidelity.navigation.SetupNavGraph
import it.puntoettore.fidelity.presentation.screen.login.LoginViewModel
import it.puntoettore.fidelity.theme.darkScheme
import it.puntoettore.fidelity.theme.highContrastDarkColorScheme
import it.puntoettore.fidelity.theme.lightScheme
import it.puntoettore.fidelity.theme.mediumContrastDarkColorScheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
) {
    val colors = if (false) {
        lightScheme
    } else {
        highContrastDarkColorScheme
    }

    val navController = rememberNavController()

    val viewModelLogin = koinViewModel<LoginViewModel>()
    val appSettings by viewModelLogin.appSettings
    val loadComplete by viewModelLogin.loadComplete

    if (loadComplete) {
        MaterialTheme(colorScheme = colors) {
            var startDestination = Screen.Login.route

            if (appSettings != null) {
                startDestination = Screen.Card.route
            }

            SetupNavGraph(navController, startDestination)
        }
    }
}