package it.puntoettore.fidelity.di

import it.puntoettore.fidelity.data.getRoomDatabase
import it.puntoettore.fidelity.presentation.screen.about.AboutViewModel
import it.puntoettore.fidelity.presentation.screen.account.AccountViewModel
import it.puntoettore.fidelity.presentation.screen.card.CardViewModel
import it.puntoettore.fidelity.presentation.screen.details.DetailsViewModel
import it.puntoettore.fidelity.presentation.screen.login.LoginViewModel
import it.puntoettore.fidelity.presentation.screen.manage.ManageViewModel
import it.puntoettore.fidelity.presentation.screen.offer.OfferViewModel
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

expect val targetModule: Module

val sharedModule = module {
    single { getRoomDatabase(get()) }
    viewModelOf(::LoginViewModel)
    viewModelOf(::CardViewModel)
    viewModelOf(::AboutViewModel)
    viewModelOf(::AccountViewModel)
    viewModelOf(::OfferViewModel)
    viewModelOf(::ManageViewModel)
    viewModelOf(::DetailsViewModel)
}

fun initializeKoin(
    config: (KoinApplication.() -> Unit)? = null
) {
    startKoin {
        config?.invoke(this)
        modules(targetModule, sharedModule)
    }
}