package it.puntoettore.fidelity.presentation.screen.cardDetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.puntoettore.fidelity.Res
import it.puntoettore.fidelity.detail
import it.puntoettore.fidelity.presentation.components.ErrorView
import it.puntoettore.fidelity.presentation.components.LoadingView
import it.puntoettore.fidelity.util.DisplayResult
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardDetailScreen(
    codice: String,
    matricola: String,
    onBackClick: () -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val viewModel = koinViewModel<CardDetailViewModel>()

    val user by viewModel.user
    val billFidelity by viewModel.billFidelity
    val datiFidelity by viewModel.datiFidelity
    val error by viewModel.error

    viewModel.getBillFidelity(matricola = matricola, codice = codice)

    // Gestione errori come in CardScreen
    LaunchedEffect(billFidelity) {
        if (billFidelity.isError()) {
            snackbarHostState.showSnackbar(
                billFidelity.getErrorMessage().error.name + " : " +
                        billFidelity.getErrorMessage().message
            )
        }
    }
    LaunchedEffect(datiFidelity) {
        if (datiFidelity.isError()) {
            snackbarHostState.showSnackbar(
                datiFidelity.getErrorMessage().error.name + " : " +
                        datiFidelity.getErrorMessage().message
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(actions = {}, navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = "Back arrow icon"
                    )
                }
            }, title = {
                Text(
                    text = stringResource(Res.string.detail),
                    style = MaterialTheme.typography.headlineLarge
                )
            })
        }, content = { it ->
            Scaffold(modifier = Modifier.padding(it).padding(start = 8.dp, end = 8.dp), topBar = {
                user?.let {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                    }
                }
            }, content = {
                billFidelity.DisplayResult(onLoading = { LoadingView() },
                    onError = { ErrorView(it) },
                    onSuccess = { data ->
                        Column(modifier = Modifier.fillMaxWidth()) {
                            // Blocco intestazione scontrino
                            if (!data.articoli.isNullOrEmpty()) {
                                LazyColumn(
                                    modifier = Modifier
                                        .padding(
                                            start = 8.dp,
                                            end = 8.dp,
                                            top = 0.dp,
                                            bottom = 8.dp
                                        )
                                        .padding(
                                            top = it.calculateTopPadding(),
                                            bottom = it.calculateBottomPadding()
                                        ), // Sfondo bianco per tutta la lista
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    // Blocco intestazione scontrino come primo elemento
                                    item {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(
                                                    start = 8.dp,
                                                    end = 8.dp,
                                                    top = 8.dp,
                                                    bottom = 0.dp
                                                ),
                                            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                ".ETTORE",
                                                fontSize = 32.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text("MODA CENTER S.R.L.")
                                            Text("Via Treviso, 71 - 31040")
                                            Text("Signoressa di Trevignano (TV)")
                                            Text("PART. IVA 00226510261")
                                            Text("Telefono 0423 670330")
                                            Text("info@puntoettore.it")
                                            Text("PUNTOETTORE.IT")
                                        }
                                    }
                                    // Lista articoli
                                    items(
                                        items = data.articoli
                                    ) { articolo ->
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(8.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = articolo.descrizione ?: "",
                                                    modifier = Modifier.weight(1f)
                                                )
                                                Text(
                                                    text = "Q.tà: ${articolo.qta}",
                                                    modifier = Modifier.padding(start = 8.dp)
                                                )
                                            }
                                            Row(
                                                modifier = Modifier.fillMaxWidth()
                                                    .padding(top = 2.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = "Sconto: ${articolo.sconto_finale}",
                                                )
                                                Text(
                                                    text = "Prezzo: ${articolo.c_netto}",
                                                )
                                            }
                                        }
                                    }
                                    // Riga totale
                                    item {
                                        val totale = data.totale
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 16.dp, end = 8.dp),
                                            horizontalArrangement = Arrangement.End
                                        ) {
                                            Text(
                                                text = "TOTALE : $totale",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 20.sp,
                                                modifier = Modifier.padding(bottom = 30.dp)
                                            )
                                        }
                                    }
                                }
                            } else {
                                ErrorView()
                            }
                        }
                    })
            }, bottomBar = {
                error?.let { error ->
                    Row {
                        Text(
                            text = error, color = Color.Red
                        )
                    }
                }
            })
        })
}