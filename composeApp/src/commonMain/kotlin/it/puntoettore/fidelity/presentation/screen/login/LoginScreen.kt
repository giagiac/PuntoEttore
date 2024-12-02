package it.puntoettore.fidelity.presentation.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mmk.kmpauth.firebase.apple.AppleButtonUiContainer
import com.mmk.kmpauth.firebase.google.GoogleButtonUiContainerFirebase
import com.mmk.kmpauth.firebase.phone.PhoneAuthContainer
import com.mmk.kmpauth.uihelper.apple.AppleSignInButton
import com.mmk.kmpauth.uihelper.google.GoogleSignInButton
import it.puntoettore.fidelity.Res
import it.puntoettore.fidelity.logo
import it.puntoettore.fidelity.title_login
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit
) {
    val viewModelAppSettings = koinViewModel<LoginViewModel>()

    LaunchedEffect(true) {

        println("LaunchedEffectApp is called")

    }

    if (viewModelAppSettings.loginSucced.value) {
        onLoginSuccess()
    }

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    Scaffold(
        content = {
//            CoilImage(
//                imageModel = { "https://app.erroridiconiazione.com/public/logo.png" },
//                imageOptions = ImageOptions(
//                    contentScale = ContentScale.FillHeight,
//                    alignment = Alignment.Center,
//                ),
//                modifier = Modifier.fillMaxHeight().blur(radiusX = 8.dp, radiusY = 8.dp)
//            )
            Image(
                painter = painterResource(Res.drawable.logo),
                contentScale = ContentScale.FillHeight,
                contentDescription = "Content description for visually impaired",
                modifier = Modifier.fillMaxHeight().blur(radiusX = 10.dp, radiusY = 10.dp)
            )
            Box(
                modifier = Modifier.padding(0.dp).fillMaxHeight(),
                contentAlignment = Alignment.Center,
            ) {
                Card(modifier = Modifier.padding(8.dp).alpha(0.96f)) {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
                            .focusRequester(focusRequester)
                    ) {
                        Row {
                            Text(
                                text = stringResource(Res.string.title_login),
                                style = MaterialTheme.typography.displayMedium,
                                fontStyle = FontStyle.Italic,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp),
                                fontSize = 50.sp,
                                color = Color.White
                            )
                        }
                        viewModelAppSettings.errorSignin.let { error ->
                            Row {
                                Text(
                                    text = error.value ?: "",
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth(),
                                    color = MaterialTheme.colorScheme.error,
                                )
                            }
                        }
                        Row(modifier = Modifier.padding(bottom = 16.dp)) {
                            GoogleButtonUiContainerFirebase(onResult = viewModelAppSettings.onFirebaseResult) {
                                GoogleSignInButton(
                                    modifier = Modifier.fillMaxWidth().height(44.dp),
                                    fontSize = 19.sp
                                ) { this.onClick() }
                            }
                        }
                        Row(modifier = Modifier.padding(bottom = 16.dp)) {
                            AppleButtonUiContainer(onResult = viewModelAppSettings.onFirebaseResult) {
                                AppleSignInButton(
                                    modifier = Modifier.fillMaxWidth().height(44.dp)
                                ) { this.onClick() }
                            }
                        }
                        Row(modifier = Modifier.padding(bottom = 16.dp)) {
                            HorizontalDivider()
                        }
                        Row {
                            // ************************** UiHelper Text Buttons *************
                            PhoneAuthContainer(
                                onResult = viewModelAppSettings.onFirebaseResult,
                                codeSent = {
                                    focusManager.clearFocus()
                                }
                            )
                        }
                    }
                }
            }
        }
    )
}
