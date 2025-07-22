package it.puntoettore.fidelity.presentation.screen.account

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import it.puntoettore.fidelity.account
import it.puntoettore.fidelity.util.DisplayResult
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    bottomBar: @Composable () -> Unit,
    onLogout: () -> Unit,
    onSupportConfirm: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val viewModel = koinViewModel<AccountViewModel>()
    val datiFidelity by viewModel.datiFidelity

    var inputText by remember { mutableStateOf("") }

    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        TopAppBar(title = {
            Text(
                text = stringResource(Res.string.account),
                style = MaterialTheme.typography.headlineLarge
            )
        })
    }, bottomBar = bottomBar, content = { it ->
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()).padding(
                top = it.calculateTopPadding(),
                bottom = it.calculateBottomPadding(),
            ).padding(8.dp)
        ) {
            viewModel.user.value?.let { user ->
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    shape = MaterialTheme.shapes.medium,
                    border = BorderStroke(0.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.padding(8.dp).fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(4.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        UserCard(user = user)
                        Button(
                            onClick = {
                                viewModel.logout()
                                onLogout()
                            }, modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text("Logout")
                        }
                    }
                }
                Column(
                    modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 8.dp, top = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Per noi è importante seguirti fino alla fine. Per qualsiasi necessità ti invitiamo a scriverci: saremo felici di aiutarti!",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Button(onClick = { onSupportConfirm() }) {
                        Text("Vai alla pagina di support")
                    }
                }
                datiFidelity.DisplayResult(onSuccess = {
                    if (it.allineata == "1") {
                        // Campo di testo input e bottone di conferma

                        Column(
                            modifier = Modifier.fillMaxWidth()
                                .padding(start = 8.dp, end = 8.dp, top = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Se ce l'hai, inserisci il tuo vecchio codice della tessera fisica per passare il credito sulla nuova fidelity card")
                            OutlinedTextField(
                                value = inputText,
                                onValueChange = { newValue ->
                                    // Consenti solo lettere e numeri
                                    if (newValue.all { it.isLetterOrDigit() }) {
                                        inputText = newValue
                                    }
                                },
                                label = { Text("Inserisci qui il tuo vecchio codice") },
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
                                Text("Conferma")
                            }
                        }
                    }
                })
            }
        }
    })
}