package it.puntoettore.fidelity.presentation.screen.cardDetail

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.unit.dp
import it.puntoettore.fidelity.Res
import it.puntoettore.fidelity.card
import it.puntoettore.fidelity.presentation.components.ErrorView
import it.puntoettore.fidelity.presentation.components.LoadingView
import it.puntoettore.fidelity.presentation.screen.component.CreditiFidelityView
import it.puntoettore.fidelity.util.DisplayResult
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardDetailScreen(
    codice: String,
    matricola: String,
    onBackClick: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val viewModel = koinViewModel<CardDetailViewModel>()
    val userDetail by viewModel.userDetail
    val creditiFidelity by viewModel.billFidelity

    Scaffold(
        topBar = {
            TopAppBar(
                actions = {

                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Back arrow icon"
                        )
                    }
                },
                title = {
                    Text(
                        text = stringResource(Res.string.card),
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
            )
        },
        content = { it ->
            Scaffold(
                modifier = Modifier.padding(it).padding(start = 8.dp, end = 8.dp),
                topBar = {
                    viewModel.user.value?.let {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {

                        }
                    }
                },
                content = {
                    creditiFidelity.DisplayResult(
                        onLoading = { LoadingView() },
                        onError = { ErrorView(it) },
                        onSuccess = { data ->

                            if (!data.articoli.isNullOrEmpty()) {
                                LazyColumn(
                                    modifier = Modifier
                                        .padding(all = 12.dp)
                                        .padding(
                                            top = it.calculateTopPadding(),
                                            bottom = it.calculateBottomPadding()
                                        ),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(
                                        items = data.articoli
                                    ) {
                                        Text(text = it.descrizione!!,
                                            modifier = Modifier.padding(1.dp)
                                        )
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