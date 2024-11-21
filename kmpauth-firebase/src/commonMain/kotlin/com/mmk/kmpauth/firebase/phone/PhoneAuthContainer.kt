package com.mmk.kmpauth.firebase.phone

import androidx.compose.runtime.Composable
import dev.gitlive.firebase.auth.FirebaseUser

@Composable
public expect fun PhoneAuthContainer(
    // onResult: (Result<FirebaseUser?>) -> Unit,
//    codeSent: (triggerResend: (Unit)) -> Unit,
//    getVerificationCode: (code: String) -> Unit,
    onResult: (Result<FirebaseUser?>) -> Unit,
    codeSent: (Unit) -> Unit,
)

public expect val isDebug: Boolean
