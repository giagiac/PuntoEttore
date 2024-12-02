package it.puntoettore.fidelity.presentation.screen.about

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mmk.kmpnotifier.notification.NotifierManager
import it.puntoettore.fidelity.api.ApiDataClient
import it.puntoettore.fidelity.api.InsultCensorClient
import it.puntoettore.fidelity.api.datamodel.UserDetail
import it.puntoettore.fidelity.api.util.onError
import it.puntoettore.fidelity.api.util.onSuccess
import it.puntoettore.fidelity.data.BookDatabase
import it.puntoettore.fidelity.domain.Book
import it.puntoettore.fidelity.domain.User
import it.puntoettore.fidelity.util.RequestState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AboutViewModel(
    private val database: BookDatabase,
    private val censorClient: InsultCensorClient,
    private val apiDataClient: ApiDataClient
) : ViewModel() {
    private var _sortedByFavorite = MutableStateFlow(false)
    val sortedByFavorite: StateFlow<Boolean> = _sortedByFavorite

    private var _books: MutableState<RequestState<List<Book>>> =
        mutableStateOf(RequestState.Loading)
    val books: State<RequestState<List<Book>>> = _books

    private var _userDetail: MutableState<RequestState<UserDetail>> =
        mutableStateOf(RequestState.Loading)
    val userDetail: State<RequestState<UserDetail>> = _userDetail

    private var _user: MutableState<User?> =
        mutableStateOf(null)
    val user: State<User?> = _user

    // TODO : portare in UI
    private var _error: MutableState<String?> = mutableStateOf(null)
    val error: State<String?> = _error

    init {
        viewModelScope.launch {
            println(censorClient.censorWords("Fuck"))

            // val idToken = database.userDao().getUserById(1).idToken

            NotifierManager.getPushNotifier().getToken()
                ?.let { apiDataClient.sendData(it) }

            database.appSettingsDao().getAppSettings().collect { appSettings ->
                if (appSettings != null) {
                    database.userDao()
                        .getUserById(appSettings._idUser).collectLatest { user ->
                            _user.value = user
                            user?.let {
                                apiDataClient.getUserDetail(user.uid).onSuccess { userDetail ->
                                    _userDetail.value = RequestState.Success(
                                        data = userDetail
                                    )
                                }.onError { error ->
                                    _userDetail.value =
                                        RequestState.Error(message = error.toString())
                                }
                            }
                        }
                } else {
                    _error.value = "Nessun utente trovato"
                }
            }
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