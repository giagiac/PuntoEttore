package com.mmk.kmpauth.uihelper.google

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch


/**
 * GoogleSignInButton [Composable] icon only.
 * This follows Google's design guidelines and can be easily customized to fit into your project.
 *
 * @param mode [GoogleButtonMode]
 */
// @OptIn(KMPAuthInternalApi::class)
@Composable
public fun PhoneSignInInputText(
    modifier: Modifier = Modifier.size(44.dp),
    shape: Shape = ButtonDefaults.shape,
    onClick: () -> Unit,
) {

    var phoneNumber by remember { mutableStateOf("") }
    var verificationCode by remember { mutableStateOf("") }
    var verificationId by remember { mutableStateOf<String?>(null) }
    // var resendToken by remember { mutableStateOf<PhoneAuthProvider.ForceResendingToken?>(null) }
    // val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Enter phone number") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            if (phoneNumber.isNotEmpty()) {
//                sendVerificationCode(
//                    activity,
//                    phoneNumber,
//                    auth,
//                    { id, token ->
//                        verificationId = id
//                        resendToken = token
//                    },
//                    { exception ->
//                        scope.launch {
//                            scaffoldState.snackbarHostState.showSnackbar("Verification failed: ${exception.message}")
//                        }
//                    }
//                )
            } else {
                scope.launch {
                    // scaffoldState.snackbarHostState.showSnackbar("Please enter a phone number")
                }
            }
        }) {
            Text("Send Code")
        }
    }
}