package it.puntoettore.fidelity.presentation.screen.details

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mmk.kmpnotifier.notification.NotifierManager
import it.puntoettore.fidelity.api.ApiDataClient
import it.puntoettore.fidelity.api.datamodel.Offers
import it.puntoettore.fidelity.api.util.onError
import it.puntoettore.fidelity.api.util.onSuccess
import it.puntoettore.fidelity.data.BookDatabase
import it.puntoettore.fidelity.domain.User
import it.puntoettore.fidelity.util.RequestState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DetailsViewModel(
    private val database: BookDatabase,
    private val apiDataClient: ApiDataClient
) : ViewModel() {
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

            NotifierManager.getPushNotifier().getToken()
                ?.let { apiDataClient.sendData(it) }

            database.appSettingsDao().getAppSettings().collect { appSettings ->
                if (appSettings != null) {
                    database.userDao()
                        .getUserById(appSettings._idUser).collectLatest { user ->
                            _user.value = user
                            user?.let {
                                apiDataClient.getOffers(user.uid).onSuccess { offers ->
                                    _offers.value = RequestState.Success(
                                        data = offers
                                    )
                                }.onError { error ->
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
}