package it.puntoettore.fidelity.presentation.screen.accountEdit

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.puntoettore.fidelity.api.ApiDataClientNextLogin
import it.puntoettore.fidelity.api.datamodel.DatiFidelityResponse
import it.puntoettore.fidelity.api.datamodel.ResponseUptAnagFidelity
import it.puntoettore.fidelity.api.util.onError
import it.puntoettore.fidelity.api.util.onSuccess
import it.puntoettore.fidelity.data.BookDatabase
import it.puntoettore.fidelity.domain.User
import it.puntoettore.fidelity.util.RequestState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class AccountEditViewModel(
    private val database: BookDatabase,
    private val apiDataClientNextLogin: ApiDataClientNextLogin
) : ViewModel() {

    private var _user: MutableState<User?> = mutableStateOf(null)
    val user: State<User?> = _user

    private var _datiFidelityResponse: MutableState<RequestState<DatiFidelityResponse>> =
        mutableStateOf(RequestState.Loading)
    val datiFidelityResponse: State<RequestState<DatiFidelityResponse>> = _datiFidelityResponse

    private var _updAnagFidelity: MutableState<RequestState<ResponseUptAnagFidelity>> =
        mutableStateOf(RequestState.Idle)
    val updAnagFidelity: State<RequestState<ResponseUptAnagFidelity>> = _updAnagFidelity

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
                        _datiFidelityResponse.value =
                            RequestState.Error(error = it.error, message = it.message)
                    }
            }
        }
    }

    fun postUpdAnagFidelity(displayName: String, phone: String, dataNascita: String) {
        viewModelScope.launch {
            // Usa LocalDate per il parsing e il formatting
            val formattedDataNascita = try {
                val parts = dataNascita.split("-")
                if (parts.size == 3) {
                    val day = parts[0].toInt()
                    val month = parts[1].toInt()
                    val year = parts[2].toInt()
                    val localDate = LocalDate(year, month, day)
                    // Compatibile con KMP: YYYYMMDD
                    localDate.year.toString().padStart(4, '0') +
                    localDate.monthNumber.toString().padStart(2, '0') +
                    localDate.dayOfMonth.toString().padStart(2, '0')
                } else {
                    dataNascita // fallback se formato inatteso
                }
            } catch (e: Exception) {
                dataNascita // fallback se errore
            }
            apiDataClientNextLogin.postUpdAnagFidelity(displayName, phone, formattedDataNascita)
                .onSuccess {
                    _updAnagFidelity.value = RequestState.Success(it)
                }.onError {
                    _updAnagFidelity.value =
                        RequestState.Error(error = it.error, message = it.message)
                }
        }
    }
}