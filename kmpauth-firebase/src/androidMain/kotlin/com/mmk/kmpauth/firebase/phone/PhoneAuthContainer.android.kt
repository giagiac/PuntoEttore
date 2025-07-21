package com.mmk.kmpauth.firebase.phone

import android.os.Parcelable
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.mmk.kmpauth.core.KMPAuthInternalApi
import com.mmk.kmpauth.core.getActivity
import com.mmk.kmpauth.firebase.BuildConfig
import dev.gitlive.firebase.auth.auth
import java.util.concurrent.TimeUnit

public actual val isDebug: Boolean = BuildConfig.DEBUG

@OptIn(KMPAuthInternalApi::class)
@Composable
public actual fun PhoneAuthContainer(
//    codeSent: (triggerResend: (Unit)) -> Unit,
//    getVerificationCode: (code: String) -> Unit,
    onResult: (Result<dev.gitlive.firebase.auth.FirebaseUser?>) -> Unit,
    codeSent: (Unit) -> Unit,
) {
    val activity = LocalContext.current.getActivity()

    val auth = FirebaseAuth.getInstance()

    val focusManager = LocalFocusManager.current

    var verificationCode = ""
    if (BuildConfig.DEBUG) {
        verificationCode = "123456"
    }

    var _verificationCode by remember { mutableStateOf(verificationCode) }
    var _verificationId by remember { mutableStateOf<String?>(null) }
    var resendToken by remember { mutableStateOf<Parcelable?>(null) }
    var errorSend by remember { mutableStateOf<String?>(null) }

    var phoneNumber by remember { mutableStateOf<String?>(null) }

    var phoneNumberEnabled by remember { mutableStateOf(true) }
    var verificationCodeEnabled by remember { mutableStateOf(false) }

//    val coroutineScope = MainScope()
//
//    val uiContainerScope = remember {
//        object : UiContainerScope {
//            override fun onClick() {
//                coroutineScope.launch {
//                    // val result = onClickSignIn(oAuthProvider)
//                    // mOnResult?.invoke(result)
//                    println("Clicked")
//                }
//            }
//        }
//    }

    Column {
        PhoneNumbers(enabled = phoneNumberEnabled, getPhoneNumber = { phone ->
            phoneNumber = phone
        })
        Text(text = errorSend ?: "", color = Color.Red)
        phoneNumber?.let { phone ->
            phoneNumberEnabled = false
            if (_verificationId == null && errorSend == null) {
                val options = PhoneAuthOptions.newBuilder(auth).setPhoneNumber(phone)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(activity!!) // Make sure your MainActivity inherits ComponentActivity
                    .setCallbacks(object :
                        PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                            // Auto-retrieval or instant validation is handled here
                            print(credential)
                        }

                        override fun onVerificationFailed(e: FirebaseException) {
                            Log.d("PhoneAuthContainer.android.kt", "Verification failed", e)
                            errorSend = e.message
                        }

                        override fun onCodeSent(
                            verificationId: String,
                            token: PhoneAuthProvider.ForceResendingToken
                        ) {
                            Log.d("PhoneAuthContainer.android.kt", "Code sent: $verificationId")
                            resendToken = token
                            _verificationId = verificationId
                            verificationCodeEnabled = true
                            focusManager.clearFocus()
                        }
                    }).build()
                PhoneAuthProvider.verifyPhoneNumber(options)
            }
            if (_verificationId != null && errorSend == null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = _verificationCode,
                        onValueChange = { _verificationCode = it },
                        label = {
                            Text("Inserisci il codice di 6 cifre inviato a $phone")
                        },
                        modifier = Modifier.weight(0.7f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        enabled = verificationCodeEnabled,
                        textStyle = TextStyle(fontSize = 24.sp)
                    )
                    Spacer(modifier = Modifier.width(8.dp)) // Spazio tra TextField e Button
                    Button(enabled = verificationCodeEnabled,
                        shape = ButtonDefaults.shape,
                        modifier = Modifier.weight(0.3f).align(Alignment.Bottom),
                        onClick = {
                            verificationCodeEnabled = false
                            val credential =
                                PhoneAuthProvider.getCredential(
                                    _verificationId!!,
                                    _verificationCode
                                )
                            auth.signInWithCredential(credential).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val user = task.result?.user
                                    if (user != null) {
                                        println("Authentication succeeded for user: ${user.phoneNumber}")
                                        onResult.invoke(Result.success(dev.gitlive.firebase.Firebase.auth.currentUser))
                                    } else {
                                        throw Exception("User is null")
                                    }
                                } else {
                                    task.exception ?: throw Exception("Authentication failed")
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