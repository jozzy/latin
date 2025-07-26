package org.latin.server.flow

import org.koin.dsl.module

val flowModule = module {

    single { FlowRunner(get()) }
    single { FlowRepository() }
}
