package it.puntoettore.fidelity

import android.content.ContentResolver
import android.net.Uri
import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.PayloadData
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration

actual fun onApplicationStartPlatformSpecific() {
    val customNotificationSound =
        Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + "it.puntoettore.fidelity" + "/" + R.raw.custom_notification_sound)
    NotifierManager.initialize(
        configuration = NotificationPlatformConfiguration.Android(
            notificationIconResId = R.drawable.ic_launcher_foreground,
            showPushNotification = true,
            notificationChannelData = NotificationPlatformConfiguration.Android.NotificationChannelData(
                soundUri = customNotificationSound.toString()
            )
        )
    )

//    NotifierManager.addListener(object : NotifierManager.Listener {
//        override fun onNewToken(token: String) {
//            println("Push Notification onNewToken: $token")
//        }
//
//        override fun onPushNotification(title: String?, body: String?) {
//            super.onPushNotification(title, body)
//            println("Push Notification notification type message is received: Title: $title and Body: $body")
//        }
//
//        override fun onPayloadData(data: PayloadData) {
//            super.onPayloadData(data)
//            println("Push Notification payloadData: $data")
//        }
//
//        override fun onNotificationClicked(data: PayloadData) {
//            super.onNotificationClicked(data)
//            println("Notification clicked, Notification payloadData: $data")
//        }
//    })
}