package com.mmk.kmpauth.firebase.phone

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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

// Per la validazione di produzione, serve una expect/actual o una libreria KMP. Qui mostro la logica, l'implementazione Android va fatta con libphonenumber-android.

public data class Country(val code: String, val flag: String, val name: String, val iso: String)

// Lista paesi UE (ISO 3166-1 alpha-2, nome, flag, prefisso)
public val euCountries: List<Country> = listOf(
    Country("+43", "🇦🇹", "Austria", "AT"),
    Country("+32", "🇧🇪", "Belgio", "BE"),
    Country("+359", "🇧🇬", "Bulgaria", "BG"),
    Country("+385", "🇭🇷", "Croazia", "HR"),
    Country("+357", "🇨🇾", "Cipro", "CY"),
    Country("+420", "🇨🇿", "Repubblica Ceca", "CZ"),
    Country("+45", "🇩🇰", "Danimarca", "DK"),
    Country("+372", "🇪🇪", "Estonia", "EE"),
    Country("+358", "🇫🇮", "Finlandia", "FI"),
    Country("+33", "🇫🇷", "Francia", "FR"),
    Country("+49", "🇩🇪", "Germania", "DE"),
    Country("+30", "🇬🇷", "Grecia", "GR"),
    Country("+36", "🇭🇺", "Ungheria", "HU"),
    Country("+353", "🇮🇪", "Irlanda", "IE"),
    Country("+39", "🇮🇹", "Italia", "IT"),
    Country("+371", "🇱🇻", "Lettonia", "LV"),
    Country("+370", "🇱🇹", "Lituania", "LT"),
    Country("+352", "🇱🇺", "Lussemburgo", "LU"),
    Country("+356", "🇲🇹", "Malta", "MT"),
    Country("+31", "🇳🇱", "Paesi Bassi", "NL"),
    Country("+48", "🇵🇱", "Polonia", "PL"),
    Country("+351", "🇵🇹", "Portogallo", "PT"),
    Country("+40", "🇷🇴", "Romania", "RO"),
    Country("+421", "🇸🇰", "Slovacchia", "SK"),
    Country("+386", "🇸🇮", "Slovenia", "SI"),
    Country("+34", "🇪🇸", "Spagna", "ES"),
    Country("+46", "🇸🇪", "Svezia", "SE"),
    Country("+1", "🇺🇸", "Stati Uniti", "US")
)

@Composable
public fun PhoneNumbers(
    phoneNumberEnabled: Boolean, getPhoneNumber: (phone: String) -> Unit
) {
    var phoneInput by remember { mutableStateOf("") }
    var selectedCountry by remember { mutableStateOf(euCountries.first { it.iso == "IT" }) }
    var expanded by remember { mutableStateOf(false) }
    var phoneNumberError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var formattedNumber by remember { mutableStateOf("") }

    // Utility: trova il prefisso UE all'inizio di una stringa
    fun findPrefix(input: String): Country? = euCountries.firstOrNull { input.startsWith(it.code) }

    // Utility: rimuove il prefisso UE all'inizio di una stringa
    fun removePrefix(input: String, prefix: String): String =
        if (input.startsWith(prefix)) input.removePrefix(prefix) else input

    // Quando l'utente digita, aggiorna la bandiera se cambia il prefisso
    fun onInputChange(input: String) {
        val trimmed = input.trim().replace(" ", "")
        val prefixCountry = if (trimmed.startsWith("+")) findPrefix(trimmed) else null
        if (prefixCountry != null && prefixCountry != selectedCountry) {
            selectedCountry = prefixCountry
        }
        phoneInput = input
        val (isValid, formatted) = validateAndFormatPhoneNumber(input, selectedCountry)
        phoneNumberError = !isValid
        formattedNumber = formatted
        errorMessage = if (!isValid && input.isNotBlank()) "Numero non valido" else ""
    }

    // Quando l'utente seleziona una bandiera, aggiorna il prefisso nel campo input
    fun onCountrySelected(newCountry: Country) {
        val trimmed = phoneInput.trim().replace(" ", "")
        val currentPrefix = if (trimmed.startsWith("+")) findPrefix(trimmed)?.code else null
        val number = if (currentPrefix != null) removePrefix(trimmed, currentPrefix) else trimmed
        // Aggiungi il nuovo prefisso solo se il campo non è vuoto
        phoneInput = if (number.isNotEmpty()) newCountry.code + number else ""
        selectedCountry = newCountry
        val (isValid, formatted) = validateAndFormatPhoneNumber(phoneInput, newCountry)
        phoneNumberError = !isValid
        formattedNumber = formatted
        errorMessage = if (!isValid && phoneInput.isNotBlank()) "Numero non valido" else ""
    }

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Dropdown bandiera
        Box {
            IconButton(onClick = { expanded = true }) {
                Text(text = selectedCountry.flag, fontSize = 28.sp)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                euCountries.forEach { country ->
                    DropdownMenuItem(text = { Text("${country.flag} ${country.name} (${country.code})") },
                        onClick = {
                            expanded = false
                            onCountrySelected(country)
                        })
                }
            }
        }
        Spacer(modifier = Modifier.padding(4.dp))
        OutlinedTextField(
            modifier = Modifier.weight(1f),
            enabled = phoneNumberEnabled,
            value = phoneInput,
            onValueChange = { input ->
                // Rimuovi spazi per la logica interna
                onInputChange(input.replace(" ", ""))
            },
            label = { Text("Numero di telefono") },
            isError = phoneNumberError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            textStyle = TextStyle(fontSize = 18.sp),
            placeholder = { Text("${selectedCountry.code} 333 123 4567") },
            singleLine = true,
            maxLines = 1
        )
    }
    if (errorMessage.isNotBlank()) {
        Text(
            errorMessage,
            color = androidx.compose.ui.graphics.Color.Red,
            modifier = Modifier.padding(start = 56.dp, top = 2.dp)
        )
    }
    if (formattedNumber.isNotBlank() && !phoneNumberError) {
        Text(
            "Numero valido: $formattedNumber",
            color = androidx.compose.ui.graphics.Color.Green,
            modifier = Modifier.padding(start = 56.dp, top = 2.dp)
        )
    }
    if (phoneNumberEnabled) {
        Button(
            onClick = {
                getPhoneNumber(formattedNumber)
            },
            enabled = !phoneNumberError && formattedNumber.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 16.dp)
        ) {
            Text("Invia OTP")
        }
    }
}

// Mappa di regex e lunghezze per i numeri mobili dei paesi UE
public val euPhonePatterns: Map<String, Pair<Regex, IntRange>> = mapOf(
    // Austria
    "AT" to Pair(Regex("^6[56789]\\d{7,9}$"), 9..11),
    // Belgio
    "BE" to Pair(Regex("^4[5-9]\\d{7}$"), 9..9),
    // Bulgaria
    "BG" to Pair(Regex("^8[7-9]\\d{7}$"), 9..9),
    // Croazia
    "HR" to Pair(Regex("^9[1257-9]\\d{7}$"), 9..9),
    // Cipro
    "CY" to Pair(Regex("^9[5-7]\\d{6}$"), 8..8),
    // Repubblica Ceca
    "CZ" to Pair(Regex("^6\\d{8}$"), 9..9),
    // Danimarca
    "DK" to Pair(Regex("^2[0-9]\\d{6}$"), 8..8),
    // Estonia
    "EE" to Pair(Regex("^5\\d{7}$"), 8..8),
    // Finlandia
    "FI" to Pair(Regex("^4[0-9]\\d{7,8}$"), 9..10),
    // Francia
    "FR" to Pair(Regex("^(6|7)\\d{8}$"), 9..9),
    // Germania
    "DE" to Pair(Regex("^1[5-7]\\d{8,9}$"), 10..11),
    // Grecia
    "GR" to Pair(Regex("^69\\d{8}$"), 10..10),
    // Ungheria
    "HU" to Pair(Regex("^(20|30|70)\\d{7}$"), 9..9),
    // Irlanda
    "IE" to Pair(Regex("^8[3-9]\\d{7}$"), 9..9),
    // Italia
    "IT" to Pair(Regex("^3\\d{8,10}$"), 10..13),
    // Lettonia
    "LV" to Pair(Regex("^2\\d{7}$"), 8..8),
    // Lituania
    "LT" to Pair(Regex("^6\\d{7}$"), 8..8),
    // Lussemburgo
    "LU" to Pair(Regex("^6[269]\\d{6,8}$"), 8..10),
    // Malta
    "MT" to Pair(Regex("^9[79]\\d{6}$"), 8..8),
    // Paesi Bassi
    "NL" to Pair(Regex("^6[1-5]\\d{7}$"), 9..9),
    // Polonia
    "PL" to Pair(Regex("^(5[0-9]|6[0-9]|7[0-9]|8[3-9])\\d{7}$"), 9..9),
    // Portogallo
    "PT" to Pair(Regex("^9[1236]\\d{7}$"), 9..9),
    // Romania
    "RO" to Pair(Regex("^7[2-8]\\d{7}$"), 9..9),
    // Slovacchia
    "SK" to Pair(Regex("^9[0-9]\\d{7}$"), 9..9),
    // Slovenia
    "SI" to Pair(Regex("^([37][01]|4[0139]|51|64|68)\\d{6}$"), 8..8),
    // Spagna
    "ES" to Pair(Regex("^[6-7]\\d{8}$"), 9..9),
    // Svezia
    "SE" to Pair(Regex("^7[0236]\\d{7}$"), 9..9),
    // Stati Uniti (US): mobile = 10 cifre, inizia con 2-9
    "US" to Pair(Regex("^[2-9][0-9]{9}$"), 10..10)
)

public fun validateAndFormatPhoneNumber(input: String, country: Country): Pair<Boolean, String> {
    val trimmed = input.trim().replace(" ", "")
    val prefix = if (trimmed.startsWith("+")) {
        euCountries.firstOrNull { trimmed.startsWith(it.code) }?.code
    } else {
        country.code
    }
    if (prefix == null) return false to ""
    val number = if (trimmed.startsWith("+")) {
        trimmed.removePrefix(prefix)
    } else {
        trimmed
    }
    if (number.any { !it.isDigit() }) return false to ""
    val patternInfo = euPhonePatterns[country.iso]
    if (patternInfo != null) {
        val (regex, lengthRange) = patternInfo
        if (number.length !in lengthRange) return false to ""
        if (!regex.matches(number)) return false to ""
    } else {
        // fallback: solo lunghezza plausibile
        if (number.length !in 8..15) return false to ""
    }
    val formatted = "$prefix$number"
    return true to formatted
}