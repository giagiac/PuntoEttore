package it.puntoettore.fidelity.presentation.screen.home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mmk.kmpnotifier.notification.NotifierManager
import it.puntoettore.fidelity.api.ApiDataClient
import it.puntoettore.fidelity.api.InsultCensorClient
import it.puntoettore.fidelity.data.BookDatabase
import it.puntoettore.fidelity.domain.Book
import it.puntoettore.fidelity.util.RequestState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel(
    private val database: BookDatabase,
    private val censorClient: InsultCensorClient,
    private val apiDataClient: ApiDataClient
) : ViewModel() {
    private var _sortedByFavorite = MutableStateFlow(false)
    val sortedByFavorite: StateFlow<Boolean> = _sortedByFavorite

    private var _books: MutableState<RequestState<List<Book>>> =
        mutableStateOf(RequestState.Loading)
    val books: State<RequestState<List<Book>>> = _books

    init {
        viewModelScope.launch {
            println(censorClient.censorWords("Fuck"))

            // val idToken = database.userDao().getUserById(1).idToken

            NotifierManager.getPushNotifier().getToken()
                ?.let { apiDataClient.sendData(it) }

            _sortedByFavorite.collectLatest { favorite ->
                if (favorite) {
                    database.bookDao()
                        .readAllBooksSortByFavorite()
                        .collectLatest { sortedBooks ->
                            _books.value = RequestState.Success(
                                data = sortedBooks.sortedBy { !it.isFavorite }
                            )
                        }
                } else {
                    database.bookDao()
                        .readAllBooks()
                        .collectLatest { allBooks ->
                            _books.value = RequestState.Success(
                                data = allBooks.sortedBy { it.isFavorite }
                            )
                        }
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