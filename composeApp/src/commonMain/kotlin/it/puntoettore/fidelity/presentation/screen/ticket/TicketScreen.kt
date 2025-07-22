package it.puntoettore.fidelity.presentation.screen.ticket

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import it.puntoettore.fidelity.Res
import it.puntoettore.fidelity.suport
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketScreen(
    bottomBar: @Composable () -> Unit, onBackClick: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val viewModel = koinViewModel<TicketViewModel>()
    val datiFidelity by viewModel.datiFidelity

    var inputText by remember { mutableStateOf("") }

    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        TopAppBar(title = {
            Text(
                text = stringResource(Res.string.suport),
                style = MaterialTheme.typography.headlineLarge
            )
        }, navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "Back arrow icon"
                )
            }
        })
    }, bottomBar = bottomBar, content = { it ->
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()).padding(
                top = it.calculateTopPadding(),
                bottom = it.calculateBottomPadding(),
            ).padding(8.dp)
        ) {
            viewModel.user.value?.let { user ->
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .padding(start = 8.dp, end = 8.dp, top = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        minLines = 4,
                        maxLines = 10,
                        singleLine = false,
                        value = inputText,
                        onValueChange = { newValue ->
                            // Consenti solo lettere e numeri
                            if (newValue.all { it.isLetterOrDigit() }) {
                                inputText = newValue
                            }
                        },
                        label = { Text("Scrivi...") },
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )
                    Button(
                        onClick = {
                            if (inputText.isNotEmpty()) {
                                // Azione di conferma qui
                                viewModel.postVecchioCliente(oldId = inputText.trim())
                            }
                        }, modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text("Invia")
                    }
                }
            }
        }
    })
}