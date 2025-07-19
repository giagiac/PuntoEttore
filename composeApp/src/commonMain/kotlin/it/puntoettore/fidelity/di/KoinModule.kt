package it.puntoettore.fidelity.di

import it.puntoettore.fidelity.data.getRoomDatabase
import it.puntoettore.fidelity.database.getMieiDati
import it.puntoettore.fidelity.presentation.screen.about.NotificationsViewModel
import it.puntoettore.fidelity.presentation.screen.account.AccountViewModel
import it.puntoettore.fidelity.presentation.screen.card.CardViewModel
import it.puntoettore.fidelity.presentation.screen.cardDetail.CardDetailViewModel
import it.puntoettore.fidelity.presentation.screen.details.DetailsViewModel
import it.puntoettore.fidelity.presentation.screen.login.LoginViewModel
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf

import org.koin.dsl.module

expect val targetModule: Module

class Pippo(val pluto:String){
    fun hello():String{
        return pluto
    }
}

val sharedModule = module {
    single { getRoomDatabase(get()) }

    //single { InsultCensorClient(httpClient = createHttpClient(get())) }

    // single { createHttpClient(tokenProvider = get()) }
//    factory<Pippo> {
//        Pippo("PIPPOOOO")
//    }
//    single {
//            p-> testMyAsyncToken(pippoProvider = p.get())
//    }
//    factory <Pippo> {
//        getMieiDati(get())
//    }

    viewModelOf(::LoginViewModel)
    viewModelOf(::CardViewModel)
    viewModelOf(::CardDetailViewModel)
    viewModelOf(::NotificationsViewModel)
    viewModelOf(::AccountViewModel)
    // viewModelOf(::OfferViewModel)
    // viewModelOf(::ManageViewModel)
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