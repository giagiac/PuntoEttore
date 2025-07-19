package it.puntoettore.fidelity.presentation.screen.login

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mmk.kmpnotifier.notification.NotifierManager
import dev.gitlive.firebase.auth.FirebaseUser
import it.puntoettore.fidelity.api.ApiDataClient
import it.puntoettore.fidelity.api.datamodel.AuthDetail
import it.puntoettore.fidelity.api.util.NetworkEError
import it.puntoettore.fidelity.api.util.onError
import it.puntoettore.fidelity.api.util.onSuccess
import it.puntoettore.fidelity.data.BookDatabase
import it.puntoettore.fidelity.domain.AppSettings
import it.puntoettore.fidelity.domain.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(
    private val database: BookDatabase, private val apiDataClient: ApiDataClient
) : ViewModel() {

    var appSettings: MutableState<AppSettings?> = mutableStateOf(null)
    var loadComplete: MutableState<Boolean> = mutableStateOf(false)
    var loginSucced: MutableState<Boolean> = mutableStateOf(false)

    var errorSignin: MutableState<String?> = mutableStateOf(null)

    init {
        viewModelScope.launch {
            database.appSettingsDao().getAppSettings()
                .collectLatest {
                    appSettings.value = it
                    loadComplete.value = true
                }
        }
    }

    val onFirebaseResult: (Result<FirebaseUser?>) -> Unit = { result ->
        if (result.isSuccess) {
            val firebaseUser = result.getOrNull()
            if (firebaseUser != null) {

                errorSignin.value = null

                apiGetAccessToken({
                    insertUser({
                        loginSucced.value = true
                    }, firebaseUser = firebaseUser, it)
                }, {
                    loginSucced.value = true
                    insertUser({
                        loginSucced.value = true
                    }, firebaseUser = firebaseUser, null)

                    errorSignin.value =
                        "Att.ne qualcosa è andato storto, potrai solo usare il qrcode per registrare i tuoi punti : ${it.name}"
                }, firebaseUser)

            }
        } else {
            // signedInUserName = ""
            errorSignin.value =
                "Att.ne qualcosa è andato storto, verifica i dati inseriti! ${result.exceptionOrNull()?.message}"
        }
    }

    private fun insertUser(
        onSuccess: () -> Unit, firebaseUser: FirebaseUser, authDetail: AuthDetail?
    ) {
        viewModelScope.launch {
            val token = NotifierManager.getPushNotifier().getToken()

            token?.let { notifierToken ->

                val user = User(
                    providerId = firebaseUser.providerId,
                    uid = firebaseUser.uid,
                    displayName = firebaseUser.displayName,
                    email = firebaseUser.email,
                    phoneNumber = firebaseUser.phoneNumber,
                    photoURL = firebaseUser.photoURL,
                    isAnonymous = firebaseUser.isAnonymous,
                    isEmailVerified = firebaseUser.isEmailVerified,
                    notifierToken = notifierToken,
                    privacy = true,
                    accessToken = authDetail?.access_token, // nel caso non ho ricevuto dal servizio di Marco mostro lo stesso
                    refreshToken = authDetail?.refresh_token // la schermata Card (con il qrcode)
                )

                if (firebaseUser.providerData.isNotEmpty()) {
                    for (providerData in firebaseUser.providerData) {
                        if (providerData.displayName != null) {
                            user.displayName = providerData.displayName
                        }
                        if (providerData.photoURL != null) {
                            user.photoURL = providerData.photoURL
                        }
                        if (providerData.phoneNumber != null) {
                            user.phoneNumber = providerData.phoneNumber
                        }
                    }
                }

                try {
                    val _id = database.userDao().insertUser(
                        user = user,
                    )
                    database.appSettingsDao().insertAppSettings(
                        appSettings = AppSettings(
                            _id = AppSettings.ID, _idUser = _id.toInt(), darkMode = true
                        )
                    )

                    onSuccess()

                } catch (e: Exception) {
                    errorSignin.value = e.message
                }
            }
        }
    }

    // Fa la chiamata all'endpoint di Marco per ottenere il token di accesso
    private fun apiGetAccessToken(onSuccess: (AuthDetail) -> Unit, onError: (NetworkEError) -> Unit, firebaseUser: FirebaseUser) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val response = apiDataClient.getAccess(firebaseUser.uid)
                    response.onSuccess {
                        onSuccess(it)
                    }
                    response.onError {
                        onError(it)
                    }
                } catch (e: Exception) {
                    errorSignin.value = e.message
                }
            }
        }
    }
}