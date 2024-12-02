package it.puntoettore.fidelity.presentation.screen.offer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mmk.kmpnotifier.notification.NotifierManager
import it.puntoettore.fidelity.Res
import it.puntoettore.fidelity.offers
import it.puntoettore.fidelity.presentation.components.ErrorView
import it.puntoettore.fidelity.presentation.components.LoadingView
import it.puntoettore.fidelity.presentation.screen.component.OfferView
import it.puntoettore.fidelity.util.DisplayResult
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfferScreen(
    bottomBar: @Composable () -> Unit,
    onOfferSelect: (url: String) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val viewModel = koinViewModel<OfferViewModel>()
    val sortedByFavorite by viewModel.sortedByFavorite.collectAsStateWithLifecycle()

    val userDetail by viewModel.offers

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.offers),
                        style = MaterialTheme.typography.headlineLarge
                    )
                },
                actions = {

                }
            )
        },
        bottomBar = bottomBar,
        content = {
            userDetail.DisplayResult(
                onLoading = { LoadingView() },
                onError = { ErrorView(it) },
                onSuccess = { data ->
                    if (data.listOffers.isNotEmpty()) {
                        Column(
                            modifier = Modifier
                                .padding(
                                    top = it.calculateTopPadding(),
                                    bottom = it.calculateBottomPadding()
                                )
                        ) {
                            Row {
                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(
                                        items = data.listOffers,
                                        key = { it.id }
                                    ) {
                                        OfferView(
                                            offer = it,
                                            onClick = {
                                                onOfferSelect(it.id)
                                            }
                                        )
                                    }
                                }
                            }
                        }


                    } else {
                        ErrorView()
                    }
                }
            )
        }
    )
}