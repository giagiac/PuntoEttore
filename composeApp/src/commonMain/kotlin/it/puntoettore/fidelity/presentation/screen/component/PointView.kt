package it.puntoettore.fidelity.presentation.screen.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.HorizontalAlignmentLine
import androidx.compose.ui.unit.dp
import it.puntoettore.fidelity.Res
import it.puntoettore.fidelity.api.datamodel.Score
import it.puntoettore.fidelity.sharp_money_bag_24
import org.jetbrains.compose.resources.painterResource

@Composable
fun PointView(
    score: Score,
    onClick: () -> Unit
) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .clickable { onClick() }) {
        Row(
            modifier = Modifier.padding(8.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(48.dp),
                painter = painterResource(Res.drawable.sharp_money_bag_24),
                contentDescription = "saved money"
            )
            Column (verticalArrangement = Arrangement.Bottom, horizontalAlignment = Alignment.End) {
                Text(
                    text = score.points,
                    maxLines = 1,
                    //overflow = TextOverflow.Ellipsis,
                    fontSize = MaterialTheme.typography.displayMedium.fontSize
                )
                Text(
                    text = score.dataScan,
                    maxLines = 2,
                    // overflow = TextOverflow.Ellipsis,
                    /// fontSize = MaterialTheme.typography.labelSmall.fontSize,
                    // fontWeight = FontWeight.Medium
                )
            }
        }
    }
}