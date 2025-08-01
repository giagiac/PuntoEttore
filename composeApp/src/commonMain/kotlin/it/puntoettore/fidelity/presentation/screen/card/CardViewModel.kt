package it.puntoettore.fidelity.presentation.screen.card

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.puntoettore.fidelity.api.ApiDataClientNextLogin
import it.puntoettore.fidelity.api.datamodel.CreditiFidelity
import it.puntoettore.fidelity.api.datamodel.DatiFidelityResponse
import it.puntoettore.fidelity.api.util.onError
import it.puntoettore.fidelity.api.util.onSuccess
import it.puntoettore.fidelity.data.BookDatabase
import it.puntoettore.fidelity.domain.User
import it.puntoettore.fidelity.util.RequestState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class CardViewModel(
    private val database: BookDatabase,
    private val apiDataClientNextLogin: ApiDataClientNextLogin
) : ViewModel() {

    private var _user: MutableState<User?> = mutableStateOf(null)
    val user: State<User?> = _user

    private var _datiFidelityResponse: MutableState<RequestState<DatiFidelityResponse>> =
        mutableStateOf(RequestState.Loading)
    val datiFidelityResponse: State<RequestState<DatiFidelityResponse>> = _datiFidelityResponse

    private var _creditiFidelity: MutableState<RequestState<List<CreditiFidelity>>> =
        mutableStateOf(RequestState.Loading)
    val creditiFidelity: State<RequestState<List<CreditiFidelity>>> = _creditiFidelity

    private var _error: MutableState<String?> = mutableStateOf(null)
    val error: State<String?> = _error

    private var _isFirstProgression: MutableState<Boolean> = mutableStateOf(true)
    var isFirstProgression: State<Boolean> = _isFirstProgression

    fun setFirstProgression(value: Boolean) {
        _isFirstProgression.value = value
    }

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

                        apiDataClientNextLogin.postCreditiFidelity()
                            .onSuccess { creditiList ->
                                // Calcolo il massimo punteggio (convertendo le stringhe a Int, ignorando i null o non numerici)
                                val maxPunteggio = creditiList.mapNotNull { it.punteggio?.toIntOrNull() }.maxOrNull() ?: 0
                                val creditiConPercentuale = creditiList.map { credito ->
                                    val punteggioInt = credito.punteggio?.toIntOrNull() ?: 0
                                    val percentuale = if (maxPunteggio > 0) (punteggioInt * 100 / maxPunteggio) else 0
                                    credito.copy(punteggioPercentuale = percentuale)
                                }
                                _creditiFidelity.value = RequestState.Success(creditiConPercentuale)
                            }
                            .onError {
                                _creditiFidelity.value = RequestState.Error(error = it.error, message = it.message)
                            }
                    }
                    .onError {
                        _creditiFidelity.value = RequestState.Error(error = it.error, message = it.message)
                    }
            }
        }
    }
}