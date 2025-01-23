package it.puntoettore.fidelity.presentation.screen.card

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mmk.kmpnotifier.notification.NotifierManager
import it.puntoettore.fidelity.Res
import it.puntoettore.fidelity.card
import it.puntoettore.fidelity.presentation.components.ErrorView
import it.puntoettore.fidelity.presentation.components.LoadingView
import it.puntoettore.fidelity.presentation.screen.component.PointView
import it.puntoettore.fidelity.score
import it.puntoettore.fidelity.util.DisplayResult
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import qrgenerator.QRCodeImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardScreen(
    bottomBar: @Composable () -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val viewModel = koinViewModel<CardViewModel>()
    val userDetail by viewModel.userDetail

    val sortedByFavorite by viewModel.sortedByFavorite.collectAsStateWithLifecycle()

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

    val infiniteTransition = rememberInfiniteTransition()
    val rotationAnimation = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(1000, easing = LinearEasing))
    )

    val brush = Brush.horizontalGradient(listOf(Color.Red, Color.Blue))

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.card),
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
            )
        },
        bottomBar = bottomBar,
        content = { it ->
            Scaffold(
                modifier = Modifier.padding(it).padding(start = 8.dp, end = 8.dp),
                topBar = {
                    viewModel.user.value?.let {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Card(
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 12.dp
                                ),
                                border = BorderStroke(1.dp, Color.Black),
                                modifier = Modifier.padding(12.dp).drawBehind {
                                    rotate(rotationAnimation.value) {
                                        drawCircle(brush, style = Stroke(50.dp.value))
                                    }
                                }
                            ) {
                                QRCodeImage(
                                    url = it.uid,
                                    contentScale = ContentScale.Fit,
                                    contentDescription = it.uid,
                                    modifier = Modifier
                                        .align(Alignment.CenterHorizontally)
                                        .size(150.dp).padding(16.dp),
                                    onSuccess = { qrImage ->

                                    },
                                    onFailure = {
                                        scope.launch {
                                            // TODO: handle error
                                        }
                                    }
                                )
                            }
                        }
                    }
                },
                content = {
                    userDetail.DisplayResult(
                        onLoading = { LoadingView() },
                        onError = { ErrorView(it) },
                        onSuccess = { data ->
                            if (data.listScores.isNotEmpty()) {
                                Column(
                                    modifier = Modifier
                                        .padding(
                                            top = it.calculateTopPadding(),
                                            bottom = it.calculateBottomPadding()
                                        )
                                ) {
                                    Row {
                                        Text(
                                            modifier = Modifier.fillMaxWidth(),
                                            textAlign = TextAlign.Center,
                                            text = data.score,
                                            style = MaterialTheme.typography.displayMedium
                                        )
                                    }
                                    Row {
                                        Text(
                                            modifier = Modifier.fillMaxWidth(),
                                            textAlign = TextAlign.Center,
                                            text = stringResource(Res.string.score),
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                    Row {
                                        LazyColumn(
                                            verticalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            items(
                                                items = data.listScores,
                                                key = { it.dataScan }
                                            ) {
                                                PointView(
                                                    score = it,
                                                    onClick = {
                                                        //onBookSelect(it._id)
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
                },
                bottomBar = {
                    viewModel.error.value?.let { error ->
                        Row {
                            Text(
                                text = error,
                                color = Color.Red
                            )
                        }
                    }
                }
            )

        })
}