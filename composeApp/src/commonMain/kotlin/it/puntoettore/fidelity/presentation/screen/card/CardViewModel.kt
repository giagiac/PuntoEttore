package it.puntoettore.fidelity.presentation.screen.card

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.puntoettore.fidelity.api.ApiDataClient
import it.puntoettore.fidelity.api.ApiDataClientNextLogin
import it.puntoettore.fidelity.api.datamodel.CreditiFidelity
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class CardViewModel(
    private val database: BookDatabase,
    private val apiDataClient: ApiDataClientNextLogin
) : ViewModel() {
    private var _sortedByFavorite = MutableStateFlow(false)
    val sortedByFavorite: StateFlow<Boolean> = _sortedByFavorite

    private var _books: MutableState<RequestState<List<Book>>> =
        mutableStateOf(RequestState.Loading)
    val books: State<RequestState<List<Book>>> = _books

    private var _userDetail: MutableState<RequestState<UserDetail>> =
        mutableStateOf(RequestState.Loading)
    val userDetail: State<RequestState<UserDetail>> = _userDetail

    private var _user: MutableState<User?> = mutableStateOf(null)
    val user: State<User?> = _user

    private var _creditiFidelity: MutableState<RequestState<List<CreditiFidelity>>> =
        mutableStateOf(RequestState.Loading)
    val creditiFidelity: State<RequestState<List<CreditiFidelity>>> = _creditiFidelity

    // TODO : portare in UI
    private var _error: MutableState<String?> = mutableStateOf(null)
    val error: State<String?> = _error

    init {
        viewModelScope.launch {

            database.appSettingsDao().getAppSettings().collect { appSettings ->
                if (appSettings != null) {
                    database.userDao().getUserById(appSettings._idUser).collectLatest { user ->
                        _user.value = user
                        user?.let {
                            apiDataClient.setUid(it.uid)
                        }
//                        user?.let {
//                            apiDataClient.setUid(it.uid)
//
//                            it.accessToken?.let { accessToken ->
//                                apiDataClient.setAccessToken(
//                                    accessToken
//                                )
//                            }
//                            it.refreshToken?.let { refreshToken ->
//                                apiDataClient.setRefreshToken(
//                                    refreshToken
//                                )
//                            }
//
//                            apiDataClient.getCreditiFidelity().onSuccess { creditiFidelity ->
//                                _creditiFidelity.value = RequestState.Success(
//                                    data = creditiFidelity
//                                )
//                            }.onError { error ->
//                                _creditiFidelity.value =
//                                    RequestState.Error(message = error.toString())
//                            }
//                        }
                    }
                } else {
                    _error.value = "Nessun utente trovato"
                }
            }


//            _sortedByFavorite.collectLatest { favorite ->
//                if (favorite) {
//                    database.bookDao()
//                        .readAllBooksSortByFavorite()
//                        .collectLatest { sortedBooks ->
//                            _books.value = RequestState.Success(
//                                data = sortedBooks.sortedBy { !it.isFavorite }
//                            )
//                        }
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

    fun loginData() {
        viewModelScope.launch {
            // apiDataClient.getAccess(token)
            user.value?.uid?.let {
                apiDataClient.getAccess(it).onSuccess { resp ->
                    println("OK : ${resp.access_token}")
                    database.userDao().getUserById(1).first().apply {
                        this?.refreshToken = resp.refresh_token
                        this?.accessToken = resp.access_token
                        this?.let {
                            database.userDao().updateUser(it)
                        }
                    }
                }.onError { error ->
                    _creditiFidelity.value =
                        RequestState.Error(message = error.toString())
                }
            }

        }
    }

    fun sendData() {
        viewModelScope.launch {
            apiDataClient.getCreditiFidelity().onSuccess { creditiFidelity ->
                _creditiFidelity.value = RequestState.Success(
                    data = creditiFidelity
                )
            }.onError { error ->
                _creditiFidelity.value =
                    RequestState.Error(message = error.toString())
            }
        }
    }

    fun invalidRefreshToken(){
        viewModelScope.launch {
            database.userDao().getUserById(1).first().apply {
                this?.accessToken = null
                this?.let { database.userDao().updateUser(it) }
            }
            _creditiFidelity.value = RequestState.Success(data = emptyList())
        }
    }
}