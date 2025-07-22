package it.puntoettore.fidelity.presentation.screen.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import coil3.size.Size
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil3.CoilImage
import it.puntoettore.fidelity.api.datamodel.CreditiFidelity
import it.puntoettore.fidelity.presentation.components.TextView
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import kotlinx.datetime.minus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.minutes

// --- DEFINIZIONI PERSONALIZZATE PER L'ITALIANO ---
val mesiItaliani = MonthNames(
    listOf(
        "Gennaio",
        "Febbraio",
        "Marzo",
        "Aprile",
        "Maggio",
        "Giugno",
        "Luglio",
        "Agosto",
        "Settembre",
        "Ottobre",
        "Novembre",
        "Dicembre"
    )
)
val giorniSettimanaItalianiAbbreviati = DayOfWeekNames(
    listOf("Lun", "Mar", "Mer", "Gio", "Ven", "Sab", "Dom")
)
// ----------------------------------------------------

fun formatDynamicDate(dateString: String?): String {
    if(dateString == null) return "Data non definita"
    val parser = LocalDateTime.Format {
        dayOfMonth(); char('/'); monthNumber(); char('/'); year()
        char(' '); hour(); char(':'); minute()
    }

    val timeZone = TimeZone.currentSystemDefault()
    val now = Clock.System.now()

    val dateInstant = try {
        parser.parse(dateString).toInstant(timeZone)
    } catch (e: Exception) {
        return "Data non valida"
    }

    val duration = now - dateInstant

    // Logica per il formato relativo
    if (duration < 1.minutes) return "pochi secondi fa"
    if (duration < 60.minutes) {
        val min = duration.inWholeMinutes
        return if (min == 1L) "un minuto fa" else "$min minuti fa"
    }

    val dateLocalDateTime = dateInstant.toLocalDateTime(timeZone)
    val nowLocalDateTime = now.toLocalDateTime(timeZone)

    if (dateLocalDateTime.date == nowLocalDateTime.date) {
        return "Oggi alle ${
            dateLocalDateTime.hour.toString().padStart(2, '0')
        }:${dateLocalDateTime.minute.toString().padStart(2, '0')}"
    }

    if (nowLocalDateTime.date.minus(1, DateTimeUnit.DAY) == dateLocalDateTime.date) {
        return "Ieri alle ${
            dateLocalDateTime.hour.toString().padStart(2, '0')
        }:${dateLocalDateTime.minute.toString().padStart(2, '0')}"
    }

    // Formattazione personalizzata per date più vecchie
    val customFormat = LocalDate.Format {
        dayOfWeek(giorniSettimanaItalianiAbbreviati)
        char(' ')
        dayOfMonth()
        char(' ')
        monthName(mesiItaliani)
        char('\'')
        yearTwoDigits(2000)
    }

    return customFormat.format(dateLocalDateTime.date)
}

@Composable
fun CreditiFidelityView(
    item: CreditiFidelity,
    onClick: () -> Unit
) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(size = 12.dp))
        .clickable { onClick() }
    ) {
        Box(modifier = Modifier.size(80.dp)) {
            CoilImage(
                modifier = Modifier
                    .clip(RoundedCornerShape(size = 12.dp))
                    .size(80.dp)
                    .padding(5.dp),
                imageModel = { /*item.matricola*/ "https://cdn-icons-png.flaticon.com/128/2374/2374851.png" },
                imageOptions = ImageOptions(
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center
                )
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier.weight(3f).padding(vertical = 6.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            TextView(
                text = formatDynamicDate(item.data_inserimento),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                fontWeight = FontWeight.Thin
            )
            TextView(
                text = item.punteggio,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontSize = MaterialTheme.typography.titleLarge.fontSize * 2,
                fontWeight = FontWeight.Medium
            )
        }
    }
}