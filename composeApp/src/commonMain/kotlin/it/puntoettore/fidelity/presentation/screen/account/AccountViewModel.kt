package it.puntoettore.fidelity.presentation.screen.account

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import it.puntoettore.fidelity.api.ApiDataClientNextLogin
import it.puntoettore.fidelity.api.datamodel.DatiFidelityResponse
import it.puntoettore.fidelity.api.datamodel.ResponseVecchioCliente
import it.puntoettore.fidelity.api.util.onError
import it.puntoettore.fidelity.api.util.onSuccess
import it.puntoettore.fidelity.data.BookDatabase
import it.puntoettore.fidelity.domain.User
import it.puntoettore.fidelity.util.RequestState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AccountViewModel(
    private val database: BookDatabase,
    private val apiDataClientNextLogin: ApiDataClientNextLogin
) : ViewModel() {

    private var _user: MutableState<User?> = mutableStateOf(null)
    val user: State<User?> = _user

    private var _datiFidelityResponse: MutableState<RequestState<DatiFidelityResponse>> =
        mutableStateOf(RequestState.Loading)
    val datiFidelityResponse: State<RequestState<DatiFidelityResponse>> = _datiFidelityResponse

    private var _vecchioCliente: MutableState<RequestState<ResponseVecchioCliente>> =
        mutableStateOf(RequestState.Idle)
    val vecchioCliente: State<RequestState<ResponseVecchioCliente>> = _vecchioCliente

    private var _error: MutableState<String?> = mutableStateOf(null)
    val error: State<String?> = _error

    init {
        viewModelScope.launch {

            val appSettings = database.appSettingsDao().getAppSettings().first()
            println("appSettings : $appSettings")
            if (appSettings == null) {
                _error.value = "Nessuna impostazione trovata"
                return@launch
            }
            _user.value = database.userDao().getUserById(appSettings._idUser).first()

            if (_user.value == null) {
                _error.value = "Nessun utente trovato"
                return@launch
            }
            if (_user.value?.uid == null) {
                _error.value = "Nessun uid trovato"
                return@launch
            }

            _user.value?.uid?.let {
                apiDataClientNextLogin.postDatiFidelity()
                    .onSuccess {
                        _datiFidelityResponse.value = RequestState.Success(it)
                    }
                    .onError {
                        _datiFidelityResponse.value = RequestState.Error(error = it.error, message = it.message)
                    }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            database.appSettingsDao().getAppSettings().collect { appSettings ->
                if (appSettings != null) {
                    database.userDao().deleteUserById(appSettings._idUser)
                    database.appSettingsDao().deleteAppSettingsById()
                    apiDataClientNextLogin.close()
                    Firebase.auth.signOut()
                    // NotifierManager.getPushNotifier().deleteMyToken()
                }
            }
        }
    }


    fun postVecchioCliente(oldId: String) {
        viewModelScope.launch {
            apiDataClientNextLogin.postVecchioCliente(oldId = oldId).onSuccess {
                _vecchioCliente.value = RequestState.Success(it)
            }.onError {
                _vecchioCliente.value = RequestState.Error(error = it.error, message = it.message)
            }
        }

    }
}