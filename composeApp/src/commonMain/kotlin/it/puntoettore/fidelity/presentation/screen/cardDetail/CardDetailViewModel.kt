package it.puntoettore.fidelity.presentation.screen.cardDetail

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.puntoettore.fidelity.api.ApiDataClient
import it.puntoettore.fidelity.api.datamodel.BillFidelity
import it.puntoettore.fidelity.api.datamodel.UserDetail
import it.puntoettore.fidelity.api.util.onError
import it.puntoettore.fidelity.api.util.onSuccess
import it.puntoettore.fidelity.data.BookDatabase
import it.puntoettore.fidelity.domain.User
import it.puntoettore.fidelity.util.RequestState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CardDetailViewModel(
    private val database: BookDatabase,
    private val apiDataClient: ApiDataClient
) : ViewModel() {

    private var _userDetail: MutableState<RequestState<UserDetail>> =
        mutableStateOf(RequestState.Loading)
    val userDetail: State<RequestState<UserDetail>> = _userDetail

    private var _user: MutableState<User?> = mutableStateOf(null)
    val user: State<User?> = _user

    private var _billFidelity: MutableState<RequestState<BillFidelity>> =
        mutableStateOf(RequestState.Loading)
    val billFidelity: State<RequestState<BillFidelity>> = _billFidelity

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

                            it.accessToken?.let { accessToken ->
                                apiDataClient.setAccessToken(
                                    accessToken
                                )
                            }
                            it.refreshToken?.let { refreshToken ->
                                apiDataClient.setRefreshToken(
                                    refreshToken
                                )
                            }
                        }
                    }
                } else {
                    _error.value = "Nessun utente trovato"
                }
            }
        }
    }

    fun fetchBillFidelity(codice: String?, matricola: String?) {

        if(codice == null || matricola == null){
            _billFidelity.value = RequestState.Error(message = "Codice o matricola non specificati")
            return
        }

        viewModelScope.launch {
            apiDataClient.getBillFidelity(codice = codice, matricola = matricola).onSuccess { creditiFidelity ->
                _billFidelity.value = RequestState.Success(
                    data = creditiFidelity
                )
            }.onError { error ->
                _billFidelity.value =
                    RequestState.Error(message = error.toString())
            }
        }
    }
}