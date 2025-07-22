package it.puntoettore.fidelity.presentation.screen.cardDetail

import androidx.compose.foundation.background
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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.puntoettore.fidelity.Res
import it.puntoettore.fidelity.card
import it.puntoettore.fidelity.detail
import it.puntoettore.fidelity.presentation.components.ErrorView
import it.puntoettore.fidelity.presentation.components.LabelValueRow
import it.puntoettore.fidelity.presentation.components.LoadingView
import it.puntoettore.fidelity.util.DisplayResult
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.pow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardDetailScreen(
    codice: String, matricola: String, onBackClick: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val viewModel = koinViewModel<CardDetailViewModel>()
    val userDetail by viewModel.userDetail
    val billFidelity by viewModel.billFidelity

    viewModel.getBillFidelity(matricola = matricola, codice = codice)

    Scaffold(topBar = {
        TopAppBar(actions = {

        }, navigationIcon = {
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
            viewModel.user.value?.let {
                Row(
                    horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()
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
                                    .padding(start = 8.dp, end = 8.dp, top = 0.dp, bottom = 8.dp)
                                    .padding(
                                        top = it.calculateTopPadding(),
                                        bottom = it.calculateBottomPadding()
                                    )
                                    .background(Color.White), // Sfondo bianco per tutta la lista
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Blocco intestazione scontrino come primo elemento
                                item {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color.White)
                                            .padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 0.dp),
                                        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            ".ETTORE",
                                            color = Color.Black,
                                            fontSize = 32.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text("MODA CENTER S.R.L.", color = Color.Black)
                                        Text("Via Treviso, 71 - 31040", color = Color.Black)
                                        Text("Signoressa di Trevignano (TV)", color = Color.Black)
                                        Text("PART. IVA 00226510261", color = Color.Black)
                                        Text("Telefono 0423 670330", color = Color.Black)
                                        Text("info@puntoettore.it", color = Color.Black)
                                        Text("PUNTOETTORE.IT", color = Color.Black)
                                    }
                                }
                                // Lista articoli
                                items(
                                    items = data.articoli
                                ) { articolo ->
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color.White)
                                            .padding(8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = articolo.descrizione ?: "",
                                                color = Color.Black,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Text(
                                                text = "Q.tà: ${articolo.qta}",
                                                color = Color.Black,
                                                modifier = Modifier.padding(start = 8.dp)
                                            )
                                        }
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "Sconto: ${articolo.sconto_finale}",
                                                color = Color.Black
                                            )
                                            Text(
                                                text = "Prezzo: ${articolo.c_netto}",
                                                color = Color.Black
                                            )
                                        }
                                    }
                                }
                                // Riga totale
                                item {
                                    val totale = data.articoli.sumOf { it.c_netto?.replace(",", ".")?.toDoubleOrNull() ?: 0.0 }
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 16.dp, end = 8.dp),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        Text(
                                            text = "TOTALE : ${totale.format(2)}",
                                            color = Color.Black,
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
            viewModel.error.value?.let { error ->
                Row {
                    Text(
                        text = error, color = Color.Red
                    )
                }
            }
        })

    })
}

fun Double.format(digits: Int): String {
    val factor = 10.0.pow(digits)
    return (kotlin.math.round(this * factor) / factor).toString()
}