package com.mmk.kmpnotifier.di

import com.mmk.kmpnotifier.notification.DesktopNotifierFactory
import com.mmk.kmpnotifier.notification.EmptyPushNotifierImpl
import com.mmk.kmpnotifier.notification.Notifier
import com.mmk.kmpnotifier.notification.PushNotifier
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.mmk.kmpnotifier.permission.EmptyPermissionUtilImpl
import com.mmk.kmpnotifier.permission.PermissionUtil
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal actual val platformModule: Module = module {
    factory { Platform.Desktop } bind Platform::class

    factory {
        val configuration =
            get<NotificationPlatformConfiguration>() as NotificationPlatformConfiguration.Desktop
        DesktopNotifierFactory.getNotifier(configuration = configuration)
    } bind Notifier::class
    factoryOf(::EmptyPermissionUtilImpl) bind PermissionUtil::class
    factoryOf(::EmptyPushNotifierImpl) bind PushNotifier::class
}