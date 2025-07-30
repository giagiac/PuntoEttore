package it.puntoettore.fidelity.presentation.screen.card

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import com.mmk.kmpnotifier.notification.NotifierManager
import it.puntoettore.fidelity.Res
import it.puntoettore.fidelity.api.datamodel.CreditiFidelity
import it.puntoettore.fidelity.card
import it.puntoettore.fidelity.presentation.components.ErrorView
import it.puntoettore.fidelity.presentation.components.LoadingView
import it.puntoettore.fidelity.presentation.screen.component.CreditiFidelityView
import it.puntoettore.fidelity.util.DisplayResult
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import qrgenerator.QRCodeImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardScreen(
    onCreditiSelect: (CreditiFidelity) -> Unit,
    bottomBar: @Composable () -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    val scope = rememberCoroutineScope()
    val viewModel = koinViewModel<CardViewModel>()
    val datiFidelity by viewModel.datiFidelityResponse
    val creditiFidelity by viewModel.creditiFidelity
    val user by viewModel.user
    val error by viewModel.error

    var myPushNotificationToken by remember { mutableStateOf("") }
    LaunchedEffect(true) {

        println("LaunchedEffectApp is called")
        NotifierManager.addListener(object : NotifierManager.Listener {
            override fun onNewToken(token: String) {
                myPushNotificationToken = token
                println("onNewToken: $token")
                // viewModel.sendData(token)
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

    Scaffold(snackbarHost = {
        SnackbarHost(snackbarHostState)
    }, topBar = {
        TopAppBar(title = {
            Text(
                text = stringResource(Res.string.card),
                style = MaterialTheme.typography.headlineLarge
            )
        })
    }, bottomBar = bottomBar, content = { it ->
        LaunchedEffect(datiFidelity) {
            if (datiFidelity.isError()) {
                snackbarHostState.showSnackbar(
                    datiFidelity.getErrorMessage().error.name + " : " + datiFidelity.getErrorMessage().message
                )
            }
        }
        LaunchedEffect(creditiFidelity) {
            if (creditiFidelity.isError()) {
                snackbarHostState.showSnackbar(
                    creditiFidelity.getErrorMessage().error.name + " : " + creditiFidelity.getErrorMessage().message
                )
            }
        }
        Column(
            modifier = Modifier.padding(
                top = it.calculateTopPadding(), bottom = it.calculateBottomPadding()
            )
        ) {
            Box {
                Column(
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    // Card custom con effetto rilievo
                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        shape = MaterialTheme.shapes.medium,
                        border = BorderStroke(0.dp, MaterialTheme.colorScheme.outline),
                        modifier = Modifier.padding(
                            start = 8.dp,
                            end = 8.dp,
                            top = 0.dp,
                            bottom = 0.dp
                        ).fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Colonna sinistra: QRCode
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Card(elevation = CardDefaults.cardElevation(
                                    defaultElevation = 12.dp
                                ),
                                    border = BorderStroke(1.dp, Color.Black),
                                    modifier = Modifier.drawBehind {
                                        rotate(rotationAnimation.value) {
                                            drawCircle(
                                                brush, style = Stroke(50.dp.value)
                                            )
                                        }
                                    }) {
                                    user?.let {
                                        QRCodeImage(url = it.uid,
                                            contentScale = ContentScale.Fit,
                                            contentDescription = it.uid,
                                            modifier = Modifier.align(Alignment.CenterHorizontally)
                                                .size(120.dp).padding(8.dp),
                                            onSuccess = { qrImage -> },
                                            onFailure = {
                                                scope.launch {
                                                    // TODO: handle error
                                                }
                                            })
                                    }
                                }
                            }
                            Column(
                                modifier = Modifier.weight(2f).padding(start = 24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                datiFidelity.DisplayResult(onSuccess = { data ->
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        // Riga 1: Nome
                                        Text(
                                            text = data.firstName,
                                            style = MaterialTheme.typography.headlineSmall,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.padding(bottom = 8.dp)
                                        )
                                        // Riga 2: Punti con animazione custom
                                        AnimatedPoints(points = data.points,
                                            viewModel.isFirstProgression.value,
                                            onFinish = {
                                                viewModel.setFirstProgression(false)
                                            })
                                        // Riga 3: Fascia con linea colorata
                                        val fasciaColor = when (data.fascia.lowercase()) {
                                            "platinum" -> Color(0xFFC0C0C0) // Platinum
                                            "bronze" -> Color(0xFFFFD700) // Gold
                                            "silver" -> Color(0xFFB0B0B0) // Silver
                                            "gold" -> Color(0xFFD2B48C) // Bronze"
                                            else -> MaterialTheme.colorScheme.primary
                                        }
                                        Column(
                                            modifier = Modifier.padding(top = 16.dp)
                                        ) {
                                            Text(
                                                text = data.fascia,
                                                style = MaterialTheme.typography.titleMedium,
                                                color = fasciaColor,
                                                modifier = Modifier.align(Alignment.CenterHorizontally)
                                            )
                                            // Linea colorata sotto il testo, altezza come il testo
                                            Box(
                                                modifier = Modifier.align(Alignment.CenterHorizontally)
                                                    .padding(top = 2.dp).fillMaxWidth(0.5f)
                                                    .height(MaterialTheme.typography.titleMedium.fontSize.value.dp / 1.5f)
                                                    .background(
                                                        fasciaColor,
                                                        shape = MaterialTheme.shapes.small
                                                    )
                                            )
                                        }
                                    }
                                })
                                // Colonna destra: Testi
                            }
                        }
                    }
                    creditiFidelity.DisplayResult(onLoading = { LoadingView() }, onError = {
                        ErrorView(it)
                    }, onSuccess = { data ->
                        LazyColumn(
                            modifier = Modifier.padding(
                                top = 8.dp,
                            ), verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(items = data, key = { it.data_inserimento!! }) {
                                CreditiFidelityView(item = it, onClick = { onCreditiSelect(it) })
                            }
                        }
                    })
                }
            }
            error.let {
                Row {
                    if (it != null) {
                        Text(
                            text = it, color = Color.Red
                        )
                    }
                }
            }
        }
    })
}

@Composable
fun AnimatedPoints(points: Int, firstProgression: Boolean, onFinish: () -> Unit) {
    var displayedPoints by remember { mutableStateOf(0) }
    val totalDuration = 3000L
    val lastSteps = 5
    val lastStepDuration = 150L
    val initialSteps = (points - lastSteps).coerceAtLeast(0)
    val initialDuration = totalDuration - (lastSteps * lastStepDuration)

    LaunchedEffect(points) {
        if (firstProgression) {
            val startTime = Clock.System.now().toEpochMilliseconds()
            var finished = false
            while (!finished) {
                val elapsed = Clock.System.now().toEpochMilliseconds() - startTime
                when {
                    elapsed < initialDuration && initialSteps > 0 -> {
                        // Fase iniziale: incremento rapido
                        val progress = (elapsed.toFloat() / initialDuration)
                        val current = (progress * initialSteps).toInt().coerceAtMost(initialSteps)
                        if (displayedPoints != current) displayedPoints = current
                    }

                    elapsed < totalDuration -> {
                        // Fase finale: ultimi 3 numeri, incremento lento
                        val finaleElapsed = elapsed - initialDuration
                        val finaleProgress =
                            (finaleElapsed.toFloat() / (lastSteps * lastStepDuration))
                        val current = (initialSteps + (finaleProgress * lastSteps)).toInt()
                            .coerceAtMost(points)
                        if (displayedPoints != current) displayedPoints = current
                    }

                    else -> {
                        // Fine animazione
                        displayedPoints = points
                        finished = true
                        onFinish()
                    }
                }
                if (!finished) {
                    kotlinx.coroutines.delay(16L) // Aggiorna ~60fps
                }
            }
        } else {
            displayedPoints = points
        }
    }

    Text(
        text = displayedPoints.toString(),
        style = MaterialTheme.typography.headlineLarge,
        color = MaterialTheme.colorScheme.primary
    )
}