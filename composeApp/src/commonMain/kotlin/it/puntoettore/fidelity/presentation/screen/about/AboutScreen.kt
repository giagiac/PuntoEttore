package it.puntoettore.fidelity.presentation.screen.about

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mmk.kmpnotifier.notification.NotificationImage
import com.mmk.kmpnotifier.notification.Notifier
import com.mmk.kmpnotifier.notification.NotifierManager
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil3.CoilImage
import it.puntoettore.fidelity.Res
import it.puntoettore.fidelity.about
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    bottomBar: @Composable () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val viewModel = koinViewModel<AboutViewModel>()
    val userDetail by viewModel.userDetail

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.about),
                        style = MaterialTheme.typography.headlineLarge
                    )
                },
            )
        },
        bottomBar = bottomBar,
        content = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()).padding(
                    top = it.calculateTopPadding(),
                    bottom = it.calculateBottomPadding(),
                ).padding(8.dp)
            ) {
                var notificationId by remember { mutableStateOf(0) }
                val notifier = remember { NotifierManager.getLocalNotifier() }
                Button(onClick={

                }){
                    Text("GO NOTIF!!!")
                }
                Button(onClick={
                    notificationId = Random.nextInt(0, Int.MAX_VALUE)
                    notifier.notify(title = "Hello", "Messaggiooo")
                }){
                    Text("CLICK HERE!!!")
                }
                Button(onClick = {
                    notificationId = Random.nextInt(0, Int.MAX_VALUE)
                    //notifier.notify(title = "Hello", "Messaggiooo")
                    notifier.notify {
                        id = notificationId
                        title = "Title from KMPNotifier"
                        body = "Body message from KMPNotifier"
                        payloadData = mapOf(
                            Notifier.KEY_URL to "https://github.com/mirzemehdi/KMPNotifier/",
                            "extraKey" to "randomValue"
                        )
                        image = NotificationImage.Url("https://github.com/user-attachments/assets/a0f38159-b31d-4a47-97a7-cc230e15d30b")
                    }
                }) {
                    Text("Send Local Notification")
                }
                Card(modifier = Modifier.fillMaxSize().padding(bottom = 8.dp)) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("Dove siamo", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Signoressa di Trevignano")
                        Text("Via Treviso, 71")
                        Text("31040, Italia")

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Orari", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Lunedì  15:30-19:30")
                        Text("Mar - Ven  9:30-12:30 | 15:30-19:30")
                        Text("Sabato  9:30-13:00 | 15:00-19:30")
                        Text("Domenica  —")

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Contatti", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("+39 0423 670330")
                        Text("info@puntoettore.it")
                    }
                }

                CoilImage(
                    modifier = Modifier
                        .fillMaxWidth(),
                    imageModel = { "https://app.erroridiconiazione.com/public/map.png" },
                    imageOptions = ImageOptions(
                        contentScale = ContentScale.Fit,
                        alignment = Alignment.Center,
                    )
                )
            }
        })
}