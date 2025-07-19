package it.puntoettore.fidelity.navigation

const val BOOK_ID_ARG = "bookId"
const val URL_ARG = "urlArg"

const val CODICE_ARG = "codiceArg"
const val MATRICOLA_ARG = "matricolaArg"

sealed class Screen(val route: String) {
    data object Login : Screen(route = "login_screen")
    data object Home : Screen(route = "home_screen")
    data object Card : Screen(route = "card_screen")
    data object Account : Screen(route = "account_screen")
    data object Offer : Screen(route = "offer_screen")
    data object About : Screen(route = "about_screen")
    data object Notifications : Screen(route = "notifications_screen")
    data object Details : Screen(route = "details_screen/{$URL_ARG}") {
        fun passUrl(url: String) = "details_screen/$url"
    }

    data object Manage : Screen(route = "manage_screen/{$BOOK_ID_ARG}") {
        fun passId(id: Int = -1) = "manage_screen/$id"
    }

    data object CardDetail :
        Screen(route = "card_detail_screen/{$CODICE_ARG}/{$MATRICOLA_ARG}") {
        fun passCodiceMatricola(codice: String = "", matricola: String = "") =
            "card_detail_screen/$codice/$matricola"
    }
}