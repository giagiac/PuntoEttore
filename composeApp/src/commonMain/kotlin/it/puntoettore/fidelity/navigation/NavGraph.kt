package it.puntoettore.fidelity.navigation

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.PayloadData
import it.puntoettore.fidelity.presentation.screen.about.AboutScreen
import it.puntoettore.fidelity.presentation.screen.about.NotificationsScreen
import it.puntoettore.fidelity.presentation.screen.account.AccountScreen
import it.puntoettore.fidelity.presentation.screen.card.CardScreen
import it.puntoettore.fidelity.presentation.screen.details.DetailsScreen
import it.puntoettore.fidelity.presentation.screen.login.LoginScreen
import it.puntoettore.fidelity.presentation.screen.manage.ManageScreen
import it.puntoettore.fidelity.presentation.screen.offer.OfferScreen
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class MyModel(val alert:String)

@Composable
fun SetupNavGraph(navController: NavHostController, startDestination: String) {

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    NotifierManager.addListener(object : NotifierManager.Listener {
        override fun onNewToken(token: String) {
            println("Push Notification onNewToken: $token")
        }

        override fun onPushNotification(title: String?, body: String?) {
            super.onPushNotification(title, body)
            println("Push Notification notification type message is received: Title: $title and Body: $body")
            coroutineScope.launch {
                val risultato = snackbarHostState.showSnackbar(
                    message = "$title: $body",
                    actionLabel = "Annulla",
                    duration = SnackbarDuration.Short
                )
                when (risultato) {
                    SnackbarResult.ActionPerformed -> {
                        // Azione "Annulla" eseguita
                        println("Snackbar annullato")
                    }
                    SnackbarResult.Dismissed -> {
                        // Snackbar chiuso normalmente
                        println("Snackbar chiuso")
                    }
                }
            }
        }

        override fun onPayloadData(data: PayloadData) {
            super.onPayloadData(data)
            println("Push Notification payloadData: ${data["aps"]}")
            coroutineScope.launch {
                val dd = data["aps"] as Map<*, *>
                val alert = dd["alert"] as Map<*, *>
                val body = alert["body"] as String
                val title = alert["title"] as String
                //val obj = Json.decodeFromString(MyModel.serializer(), dd.toString())
                val risultato = snackbarHostState.showSnackbar(
                    message = "$title $body",
                    actionLabel = "Annulla",
                    duration = SnackbarDuration.Indefinite
                )
                when (risultato) {
                    SnackbarResult.ActionPerformed -> {
                        // Azione "Annulla" eseguita
                        println("Snackbar annullato")
                    }
                    SnackbarResult.Dismissed -> {
                        // Snackbar chiuso normalmente
                        println("Snackbar chiuso")
                    }
                }
            }
        }

        override fun onNotificationClicked(data: PayloadData) {
            super.onNotificationClicked(data)
            println("Notification clicked, Notification payloadData: $data")
        }
    })

    NavHost(
        navController = navController,
        startDestination = startDestination, // Screen.Login.route
    ) {
        composable(route = Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Card.route) {
                        popUpTo(Screen.Login.route) { // toglie dalla storia il LOGIN
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(route = Screen.Card.route) {
            CardScreen(
                bottomBar = {
                    BottomBar(navController)
                },
                snackbarHostState = snackbarHostState
            )
        }
        composable(route = Screen.Notifications.route) {
            NotificationsScreen (
                bottomBar = {
                    BottomBar(navController)
                }
            )
        }
        composable(route = Screen.Account.route) {
            AccountScreen(
                bottomBar = {
                    BottomBar(navController)
                },
                onNotificationsSelect = {
                    navController.navigate(Screen.Notifications.route)
                },
                onLogout = {
                    navController.navigate(Screen.Login.route)
                    {
                        navController.popBackStack(Screen.Account.route, true)
                    }
                }
            )
        }
        composable(route = Screen.Offer.route) {
            OfferScreen(
                bottomBar = {
                    BottomBar(navController)
                },
                onOfferSelect = {
                    navController.navigate(Screen.Details.passUrl(it))
                },
            )
        }
        composable(route = Screen.About.route) {
            AboutScreen(
                bottomBar = {
                    BottomBar(navController)
                }
            )
        }
        composable(route = Screen.Notifications.route) {
            NotificationsScreen(
                bottomBar = {
                    BottomBar(navController)
                }
            )
        }
//        composable(route = Screen.Home.route) {
//            CardScreen(
//                onBookSelect = {
//                    navController.navigate(Screen.Details.passId(it))
//                },
//                onCreateClick = {
//                    navController.navigate(Screen.Manage.passId())
//                },
//                bottomBar = {
//                    BottomBar(navController)
//                }
//            )
//        }
        composable(
            route = Screen.Manage.route,
            arguments = listOf(
                navArgument(
                    name = BOOK_ID_ARG
                ) {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) {
            val id = it.arguments?.getInt(BOOK_ID_ARG) ?: -1
            ManageScreen(
                id = id,
                onBackClick = { navController.navigateUp() },
                bottomBar = {
                    BottomBar(navController)
                }
            )
        }
        composable(
            route = Screen.Details.route,
            arguments = listOf(
                navArgument(
                    name = URL_ARG
                ) {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) {
            val id = it.arguments?.getString(URL_ARG) ?: ""
            DetailsScreen(
                id = id,
                onEditClick = {
                    navController.navigate(Screen.Details.passUrl(id))
                },
                onBackClick = { navController.navigateUp() }
            )
        }
    }
}