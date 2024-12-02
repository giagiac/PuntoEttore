package it.puntoettore.fidelity.presentation.screen.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil3.CoilImage
import it.puntoettore.fidelity.api.datamodel.Offer

@Composable
fun OfferDetailView(
    offer: Offer
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(size = 12.dp))
    ) {
        Column(
            modifier = Modifier.weight(3f).padding(6.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.End
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                CoilImage(
                    modifier = Modifier
                        .clip(RoundedCornerShape(size = 12.dp))

                        .fillMaxWidth(),
                    imageModel = { offer.url },
                    imageOptions = ImageOptions(
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.Center
                    )
                )
            }
            Text(
                text = offer.points,
                maxLines = 1,
                //overflow = TextOverflow.Ellipsis,
                fontSize = MaterialTheme.typography.displayMedium.fontSize
            )
            Text(
                text = offer.dataExpire,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontSize = MaterialTheme.typography.labelSmall.fontSize,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = offer.detail,
                overflow = TextOverflow.Ellipsis,
                fontSize = MaterialTheme.typography.labelMedium.fontSize,
                fontWeight = FontWeight.Medium
            )
        }
    }
}