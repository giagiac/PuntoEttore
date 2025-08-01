package it.puntoettore.fidelity.di

import it.puntoettore.fidelity.data.getRoomDatabase
import it.puntoettore.fidelity.presentation.screen.about.AboutViewModel
import it.puntoettore.fidelity.presentation.screen.about.NotificationsViewModel
import it.puntoettore.fidelity.presentation.screen.account.AccountViewModel
import it.puntoettore.fidelity.presentation.screen.accountEdit.AccountEditViewModel
import it.puntoettore.fidelity.presentation.screen.card.CardViewModel
import it.puntoettore.fidelity.presentation.screen.cardDetail.CardDetailViewModel
import it.puntoettore.fidelity.presentation.screen.details.DetailsViewModel
import it.puntoettore.fidelity.presentation.screen.login.LoginViewModel
import it.puntoettore.fidelity.presentation.screen.offer.OfferViewModel
import it.puntoettore.fidelity.presentation.screen.ticket.TicketViewModel
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
    viewModelOf(::CardDetailViewModel)
    viewModelOf(::NotificationsViewModel)
    viewModelOf(::TicketViewModel)
    viewModelOf(::AccountViewModel)
    viewModelOf(::AccountEditViewModel)
    viewModelOf(::DetailsViewModel)
    viewModelOf(::OfferViewModel)
    viewModelOf(::AboutViewModel)
}

fun initializeKoin(
    config: (KoinApplication.() -> Unit)? = null
) {
    startKoin {
        config?.invoke(this)
        modules(targetModule, sharedModule)
    }
}