package it.puntoettore.fidelity.presentation.screen.cardDetail

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.puntoettore.fidelity.api.ApiDataClientNextLogin
import it.puntoettore.fidelity.api.datamodel.BillFidelity
import it.puntoettore.fidelity.api.datamodel.DatiFidelityResponse
import it.puntoettore.fidelity.api.util.onError
import it.puntoettore.fidelity.api.util.onSuccess
import it.puntoettore.fidelity.data.BookDatabase
import it.puntoettore.fidelity.domain.User
import it.puntoettore.fidelity.util.RequestState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class CardDetailViewModel(
    private val database: BookDatabase,
    private val apiDataClientNextLogin: ApiDataClientNextLogin
) : ViewModel() {

    private var _user: MutableState<User?> = mutableStateOf(null)
    val user: State<User?> = _user

    private var _billFidelity: MutableState<RequestState<BillFidelity>> =
        mutableStateOf(RequestState.Loading)
    val billFidelity: State<RequestState<BillFidelity>> = _billFidelity

    private var _datiFidelityResponse: MutableState<RequestState<DatiFidelityResponse>> =
        mutableStateOf(RequestState.Loading)
    val datiFidelityResponse: State<RequestState<DatiFidelityResponse>> = _datiFidelityResponse

    // TODO : portare in UI
    private var _error: MutableState<String?> = mutableStateOf(null)
    val error: State<String?> = _error

    fun getBillFidelity(matricola:String, codice:String) {
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
                _error.value = "Nessun utente trovato"
                return@launch
            }

            _user.value?.uid?.let {
                apiDataClientNextLogin.postDatiFidelity()
                    .onSuccess {
                        _datiFidelityResponse.value = RequestState.Success(it)

                        apiDataClientNextLogin.postBillFidelity(matricola = matricola, codice = codice)
                            .onSuccess {
                                _billFidelity.value = RequestState.Success(it)
                            }
                            .onError {
                                _billFidelity.value = RequestState.Error(error = it.error, message = it.message)

                            }
                    }
                    .onError {
                        _datiFidelityResponse.value = RequestState.Error(error = it.error, message = it.message)
                    }
            }
        }
    }
}