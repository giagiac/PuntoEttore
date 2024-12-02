package it.puntoettore.fidelity.presentation.screen.account

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mmk.kmpnotifier.notification.NotifierManager
import it.puntoettore.fidelity.Res
import it.puntoettore.fidelity.account
import it.puntoettore.fidelity.presentation.components.ErrorView
import it.puntoettore.fidelity.presentation.components.LoadingView
import it.puntoettore.fidelity.util.DisplayResult
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    bottomBar: @Composable () -> Unit, onLogout: () -> Unit
) {
    val viewModel = koinViewModel<AccountViewModel>()
    val userDetail by viewModel.userDetail

    var myPushNotificationToken by remember { mutableStateOf("") }
    LaunchedEffect(true) {

        println("LaunchedEffectApp is called")
        NotifierManager.addListener(object : NotifierManager.Listener {
            override fun onNewToken(token: String) {
                myPushNotificationToken = token
                println("onNewToken: $token")
                viewModel.sendData(token)
            }
        })
        myPushNotificationToken = NotifierManager.getPushNotifier().getToken() ?: ""
    }

    Scaffold(modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = {
                Text(
                    text = stringResource(Res.string.account),
                    style = MaterialTheme.typography.headlineLarge
                )
            })
        },
        bottomBar = bottomBar,
        content = { it ->
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()).padding(
                    top = it.calculateTopPadding(),
                    bottom = it.calculateBottomPadding(),
                ).padding(8.dp)
            ) {
                Row {
                    viewModel.user.value?.let { user ->
                        Column {
                            Row {
                                UserCard(user = user)
                            }
                            Row(
                                modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
                                    .padding(top = 8.dp, bottom = 8.dp)
                            ) {
                                Button(onClick = {
                                    viewModel.logout()
                                    onLogout()
                                }, content = { Text("Logout") })
                            }
                        }
                    }
                }
                Row {
                    userDetail.DisplayResult(onLoading = { LoadingView() },
                        onError = { ErrorView(it) },
                        onSuccess = { data ->
                            UserDetailCard(userDetail = data)
                        })
                }
            }
        })
}