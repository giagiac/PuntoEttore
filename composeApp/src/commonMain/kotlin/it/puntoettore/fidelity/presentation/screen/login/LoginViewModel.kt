package it.puntoettore.fidelity.presentation.screen.login

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mmk.kmpnotifier.notification.NotifierManager
import dev.gitlive.firebase.auth.FirebaseUser
import it.puntoettore.fidelity.data.BookDatabase
import it.puntoettore.fidelity.domain.AppSettings
import it.puntoettore.fidelity.domain.User
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoginViewModel(
    private val database: BookDatabase
) : ViewModel() {

    var appSettings: MutableState<AppSettings?> = mutableStateOf(null)
    var loadComplete: MutableState<Boolean> = mutableStateOf(false)

    var loginSucced: MutableState<Boolean> = mutableStateOf(false)

    var errorSignin: MutableState<String?> = mutableStateOf(null)

    init {
        viewModelScope.launch {
            database.appSettingsDao()
                .getAppSettings()
//                .onStart {
//                    emit(null)
//
//                }
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

                insertUser({
                    loginSucced.value = true
                }, firebaseUser = firebaseUser)
            }
        } else {
            // signedInUserName = ""
            errorSignin.value =
                "Att.ne qualcosa è andato storto, verifica i dati inseriti! ${result.exceptionOrNull()?.message}"
        }
    }

    private fun insertUser(
        onSuccess: () -> Unit,
        firebaseUser: FirebaseUser
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
                    privacy = true
                )

                if(firebaseUser.providerData.isNotEmpty()){
                    for(providerData in firebaseUser.providerData){
                        if(providerData.displayName != null){
                            user.displayName = providerData.displayName
                        }
                        if(providerData.photoURL != null){
                            user.photoURL = providerData.photoURL
                        }
                        if(providerData.phoneNumber != null){
                            user.phoneNumber = providerData.phoneNumber
                        }
                    }
                }

                try {
                    val _id = database.userDao()
                        .insertUser(
                            user = user,
                        )
                    database.appSettingsDao().insertAppSettings(
                        appSettings = AppSettings(
                            _id = AppSettings.ID,
                            _idUser = _id.toInt(),
                            darkMode = true
                        )
                    )
                    onSuccess()
                } catch (e: Exception) {
                    errorSignin.value = e.message
                }
            }
        }
    }
}