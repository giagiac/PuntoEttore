package it.puntoettore.fidelity

import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.PayloadData
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration

actual fun onApplicationStartPlatformSpecific() {

    NotifierManager.initialize(
        NotificationPlatformConfiguration.Ios(
            showPushNotification = true,
            askNotificationPermissionOnStart = true,
            notificationSoundName = "custom_notification_sound.wav"
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