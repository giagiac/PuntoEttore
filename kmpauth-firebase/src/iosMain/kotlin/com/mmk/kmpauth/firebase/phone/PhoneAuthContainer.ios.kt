package com.mmk.kmpauth.firebase.phone

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.PhoneAuthProvider
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.auth.ios
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSError

//On iOS this is needed for some reason, app is recomposed again when navigate to OAuth Screen.
// rememberUpdatedState doesn't solve the problem

// iOS
public actual val isDebug: Boolean = false

@OptIn(ExperimentalForeignApi::class)
@Composable
public actual fun PhoneAuthContainer(
//    codeSent: (triggerResend: (Unit)) -> Unit,
//    getVerificationCode: (code: String) -> Unit,
    onResult: (Result<FirebaseUser?>) -> Unit,
    codeSent: (Unit) -> Unit,
) {
    val auth = Firebase.auth.ios

    var _verificationCode by remember { mutableStateOf("") }
    var _verificationId by remember { mutableStateOf<String?>(null) }
    var resendToken by remember { mutableStateOf<String?>(null) }
    var errorSend by remember { mutableStateOf<String?>(null) }

    var phoneNumber by remember { mutableStateOf<String?>(null) }

    var phoneNumberEnabled by remember { mutableStateOf(true) }
    var verificationCodeEnabled by remember { mutableStateOf(true) }

    val focusManager = LocalFocusManager.current

    Column {
        PhoneNumbers(enabled = phoneNumberEnabled, getPhoneNumber = { phone ->
            phoneNumber = phone
        })
        Text(text = errorSend ?: "", color = Color.Red)
        phoneNumber?.let { phone ->
            phoneNumberEnabled = false
            if (_verificationId == null && errorSend == null) {
                PhoneAuthProvider().ios.verifyPhoneNumber(
                    phoneNumber = phone,
                    null,
                    completion = { token: String?, error: NSError? ->
                        if (error != null) {
                            errorSend =
                                "Errore di autenticazione : " + error.localizedFailureReason()
                            //return@verifyPhoneNumber
                        } else {
                            // println("Verification id -> $token")
                            println("Token: $token")
                            _verificationId = token
                            focusManager.clearFocus()
                        }
                    }
                )
            }
            if (_verificationId != null && errorSend == null) {
                Row {
                    OutlinedTextField(
                        enabled = verificationCodeEnabled,
                        value = _verificationCode,
                        onValueChange = { _verificationCode = it },
                        label = { Text("Inserisci il codice di 6 cifre inviato a $phone") },
                        modifier = Modifier.weight(0.7f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(enabled = verificationCodeEnabled,
                        modifier = Modifier.weight(0.3f),
                        onClick = {
                            verificationCodeEnabled = false
                            val authCredential =
                                PhoneAuthProvider().ios.credentialWithVerificationID(
                                    _verificationId!!,
                                    _verificationCode
                                )

                            auth.signInWithCredential(authCredential) { result, signInError ->
                                println("Result: ${result?.user()?.phoneNumber()}")
                                println("Error: $signInError")
                                if (result != null) onResult(Result.success(Firebase.auth.currentUser))
                                else {
                                    errorSend = signInError?.localizedFailureReason
                                    onResult(Result.failure(IllegalStateException(signInError?.localizedFailureReason)))
                                }
                            }

                        }) {
                        Text(text = "Conferma")
                    }
                }
            }
        }
    }
}