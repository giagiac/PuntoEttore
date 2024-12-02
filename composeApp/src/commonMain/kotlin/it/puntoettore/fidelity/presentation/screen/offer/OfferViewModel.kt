package it.puntoettore.fidelity.presentation.screen.offer

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mmk.kmpnotifier.notification.NotifierManager
import it.puntoettore.fidelity.api.ApiDataClient
import it.puntoettore.fidelity.api.InsultCensorClient
import it.puntoettore.fidelity.api.datamodel.Offers
import it.puntoettore.fidelity.api.util.onError
import it.puntoettore.fidelity.api.util.onSuccess
import it.puntoettore.fidelity.data.BookDatabase
import it.puntoettore.fidelity.domain.User
import it.puntoettore.fidelity.util.RequestState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class OfferViewModel(
    private val database: BookDatabase,
    private val censorClient: InsultCensorClient,
    private val apiDataClient: ApiDataClient
) : ViewModel() {
    private var _sortedByFavorite = MutableStateFlow(false)
    val sortedByFavorite: StateFlow<Boolean> = _sortedByFavorite

    private var _offers: MutableState<RequestState<Offers>> =
        mutableStateOf(RequestState.Loading)
    val offers: State<RequestState<Offers>> = _offers

    private var _user: MutableState<User?> =
        mutableStateOf(null)
    val user: State<User?> = _user

    // TODO : portare in UI
    private var _error: MutableState<String?> = mutableStateOf(null)
    val error: State<String?> = _error

    init {
        viewModelScope.launch {
            println(censorClient.censorWords("Fuck"))

            NotifierManager.getPushNotifier().getToken()
                ?.let { apiDataClient.sendData(it) }

            database.appSettingsDao().getAppSettings().collect { appSettings ->
                if (appSettings != null) {
                    database.userDao()
                        .getUserById(appSettings._idUser).collectLatest { user ->
                            _user.value = user
                            user?.let{
                                apiDataClient.getOffers(user.uid).onSuccess { offers ->
                                    _offers.value = RequestState.Success(
                                        data = offers
                                    )
                                }.onError { error ->
                                    _offers.value = RequestState.Error(message = error.toString())
                                }
                            }
                        }
                } else {
                    _error.value = "Nessun utente trovato"
                }
            }

//            _sortedByFavorite.collectLatest { favorite ->
//                if (favorite) {
//
//                } else {
//                    database.bookDao()
//                        .readAllBooks()
//                        .collectLatest { allBooks ->
//                            _books.value = RequestState.Success(
//                                data = allBooks.sortedBy { it.isFavorite }
//                            )
//                        }
//                }
//            }
        }
    }

    fun toggleSortByFavorite() {
        _sortedByFavorite.value = !_sortedByFavorite.value
    }

    fun sendData(token: String) {
        viewModelScope.launch {
            apiDataClient.sendData(token)
        }
    }
}