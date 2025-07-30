package it.puntoettore.fidelity.presentation.screen.accountEdit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.unit.dp
import it.puntoettore.fidelity.Res
import it.puntoettore.fidelity.account_edit
import it.puntoettore.fidelity.presentation.components.ErrorView
import it.puntoettore.fidelity.util.DisplayResult
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountEditScreen(
    bottomBar: @Composable () -> Unit,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit // nuova callback
) {
    val scope = rememberCoroutineScope()
    val viewModel = koinViewModel<AccountEditViewModel>()
    val datiFidelity by viewModel.datiFidelityResponse
    val updAnagFidelity by viewModel.updAnagFidelity

    // Gestione errori come CardScreen
    LaunchedEffect(datiFidelity) {
        if (datiFidelity.isError()) {
            snackbarHostState.showSnackbar(
                datiFidelity.getErrorMessage().error.name + " : " +
                        datiFidelity.getErrorMessage().message
            )
        }
    }

    // Gestione successo salvataggio anagrafica
    LaunchedEffect(updAnagFidelity) {
        if (updAnagFidelity.isSuccess()) {
            snackbarHostState.showSnackbar(
                "Salvataggio effettuato"
            )
            kotlinx.coroutines.delay(500)
            onBackClick()
        } else if (updAnagFidelity.isError()) {
            snackbarHostState.showSnackbar(
                updAnagFidelity.getErrorMessage().error.name + " : " +
                        updAnagFidelity.getErrorMessage().message
            )
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Back arrow icon"
                        )
                    }
                    Text(
                        text = stringResource(Res.string.account_edit),
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
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
                viewModel.user.value?.let { user ->
                    Column(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        datiFidelity.DisplayResult(onSuccess = {
                            // Campi di testo input e bottone di conferma
                            var phone by remember { mutableStateOf(it.phone ?: "") }
                            var birthDate by remember { mutableStateOf(it.dataNascita ?: "") }
                            var displayName by remember { mutableStateOf(it.firstName ?: "") }
                            var phoneError by remember { mutableStateOf<String?>(null) }
                            var birthDateError by remember { mutableStateOf<String?>(null) }
                            var displayNameError by remember { mutableStateOf<String?>(null) }

                            fun validatePhone(input: String): String? {
                                return if (input.isBlank()) {
                                    "Il telefono non può essere vuoto"
                                } else if (!input.matches(Regex("^[0-9 ]+"))) {
                                    "Il telefono deve contenere solo numeri"
                                } else null
                            }

                            fun validateBirthDate(input: String): String? {
                                if (input.isBlank()) {
                                    return "La data di nascita non può essere vuota"
                                }
                                val parts = input.split("-")
                                if (parts.size != 3) return "Formato data non valido (DD-MM-YYYY)"
                                val day = parts[0].toIntOrNull() ?: return "Giorno non valido"
                                val month = parts[1].toIntOrNull() ?: return "Mese non valido"
                                val year = parts[2].toIntOrNull() ?: return "Anno non valido"
                                if (month !in 1..12) return "Mese non valido (1-12)"
                                // Prova parsing con LocalDate (accetta anche 1/1/2020)
                                return try {
                                    LocalDate(year, month, day)
                                    null // Data valida
                                } catch (e: Exception) {
                                    "Data non valida per il mese selezionato"
                                }
                            }

                            fun validateDisplayName(input: String): String? {
                                return if (input.trim().isEmpty()) {
                                    "Il nome non può essere vuoto"
                                } else null
                            }

                            Column(
                                modifier = Modifier.fillMaxWidth()
                                    .padding(start = 8.dp, end = 8.dp, top = 8.dp)
                            ) {
                                OutlinedTextField(
                                    value = displayName,
                                    onValueChange = {
                                        displayName = it
                                        displayNameError = validateDisplayName(it)
                                    },
                                    label = { Text("Nome") },
                                    isError = displayNameError != null,
                                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                                    singleLine = true
                                )
                                if (displayNameError != null) {
                                    Text(
                                        displayNameError!!,
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                Modifier.padding(vertical = 12.dp)
                                OutlinedTextField(
                                    value = phone,
                                    onValueChange = {
                                        phone = it
                                        phoneError = validatePhone(it)
                                    },
                                    label = { Text("Telefono") },
                                    isError = phoneError != null,
                                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                                    singleLine = true
                                )
                                if (phoneError != null) {
                                    Text(
                                        phoneError!!,
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                Modifier.padding(vertical = 12.dp)
                                OutlinedTextField(
                                    value = birthDate,
                                    onValueChange = {
                                        birthDate = it
                                        birthDateError = validateBirthDate(it)
                                    },
                                    label = { Text("Data di nascita (DD/MM/YYYY)") },
                                    isError = birthDateError != null,
                                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                                    singleLine = true
                                )
                                if (birthDateError != null) {
                                    Text(
                                        birthDateError!!,
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                Modifier.padding(vertical = 12.dp)
                                Button(
                                    onClick = {
                                        viewModel.postUpdAnagFidelity(
                                            displayName = displayName.trim(),
                                            phone = phone,
                                            dataNascita = birthDate
                                        )
                                    },
                                    enabled = displayNameError == null && phoneError == null && birthDateError == null && displayName.isNotBlank() && phone.isNotBlank() && birthDate.isNotBlank(),
                                    modifier = Modifier.padding(top = 16.dp)
                                ) {
                                    Text("Conferma")
                                }
                            }
                        }, onError = {
                            ErrorView(it)
                        })
                    }
                }
            }
        })
}