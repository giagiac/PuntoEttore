package it.puntoettore.fidelity.presentation.screen.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import it.puntoettore.fidelity.Res
import it.puntoettore.fidelity.api.datamodel.Offer
import it.puntoettore.fidelity.detail
import it.puntoettore.fidelity.presentation.components.ErrorView
import it.puntoettore.fidelity.presentation.components.LoadingView
import it.puntoettore.fidelity.presentation.screen.component.OfferDetailView
import it.puntoettore.fidelity.util.DisplayResult
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DetailsScreen(
    id: String,
    onEditClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val viewModel = koinViewModel<DetailsViewModel>()
    val offers by viewModel.offers

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.detail),
                        style = MaterialTheme.typography.headlineLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Back arrow icon"
                        )
                    }
                },
                actions = {

                }
            )
        }
    ) { padding ->
        offers.DisplayResult(
            onLoading = { LoadingView() },
            onError = { ErrorView(it) },
            onSuccess = { data ->
                val offer: Offer? = data.listOffers.findLast { it.id == id }
                if (offer != null) {
                    Column(
                        modifier = Modifier
                            .padding(
                                top = padding.calculateTopPadding(),
                                bottom = padding.calculateBottomPadding()
                            )
                    ) {
                        Row {
                            OfferDetailView(
                                offer = offer
                            )
                        }
                    }
                } else {
                    ErrorView()
                }
            }
        )
    }
}