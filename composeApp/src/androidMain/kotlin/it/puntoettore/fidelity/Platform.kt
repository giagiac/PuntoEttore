package it.puntoettore.fidelity

import android.content.ContentResolver
import android.net.Uri
import com.mmk.kmpnotifier.notification.NotifierManager
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
}