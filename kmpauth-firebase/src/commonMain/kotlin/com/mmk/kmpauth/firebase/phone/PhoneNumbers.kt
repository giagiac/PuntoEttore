package com.mmk.kmpauth.firebase.phone

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

public data class Country(val code: String, val flag: String, val name: String)

@Composable
public fun PhoneNumbers(enabled: Boolean, getPhoneNumber: (phone: String) -> Unit) {
    val countries = listOf(
        Country("+39", "🇮🇹", "Italia"),
        Country("+1", "🇺🇸", "Stati Uniti"),
        // Aggiungi altri paesi qui...
    )

    var _phoneNumber = ""
    var indexCountrySelected = 0
    if (isDebug) {
        _phoneNumber = "6505551234"
        indexCountrySelected = 1 // Stati Uniti
    }

    var selectedCountry by remember { mutableStateOf(countries[indexCountrySelected]) }

    var phoneNumber by remember { mutableStateOf(_phoneNumber) }
    var phoneNumberError by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Campo di testo con DropdownMenu per la selezione del prefisso internazionale
            Box(modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    enabled = enabled,
                    value = "${selectedCountry.flag} ${selectedCountry.name} (${selectedCountry.code})",
                    onValueChange = { },
                    label = { Text("Prefisso internazionale") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Filled.ArrowDropDown, contentDescription = "Mostra opzioni")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(fontSize = 18.sp)
                )
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    countries.forEach { country ->
                        DropdownMenuItem(text = {
                            Text(text = "${country.flag} ${country.name} (${country.code})")
                        }, onClick = {
                            selectedCountry = country
                            phoneNumber = "" // Resetta il numero di telefono quando si cambia paese
                            expanded = false
                        })
                    }
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Campo di testo per il numero di telefono
            OutlinedTextField(
                modifier = Modifier.weight(0.7f),
                enabled = enabled,
                value = phoneNumber,
                onValueChange = { phone ->
                    phoneNumber = phone.filter { it.isDigit() }
                    if (isValidPhoneNumber(selectedCountry.code, phoneNumber)) {
                        phoneNumberError = false
                    } else {
                        phoneNumberError = true
                    }
                },
                label = { Text("Numero di telefono") },
                isError = phoneNumberError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                // modifier = Modifier.weight(1f),
                textStyle = TextStyle(fontSize = 18.sp)
            )
            Spacer(modifier = Modifier.width(8.dp)) // Spazio tra TextField e Button
            Button(modifier = Modifier.weight(0.3f).align(Alignment.Bottom),
                enabled = enabled && isValidPhoneNumber(selectedCountry.code, phoneNumber),
                onClick = {
                    getPhoneNumber(selectedCountry.code + phoneNumber)
                }) {
                Text("Ricevi OTP")
            }
        }
    }
}

// Funzione di esempio per la validazione del numero di telefono
// TODO: Sostituisci con una logica di validazione più accurata
public fun isValidPhoneNumber(countryCode: String, phoneNumber: String): Boolean {
    return phoneNumber.length in 8..15
}