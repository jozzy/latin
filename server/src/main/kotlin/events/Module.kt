package org.latin.server.events

import org.koin.dsl.module

val eventModule = module {

    single { EventHub() }
}
