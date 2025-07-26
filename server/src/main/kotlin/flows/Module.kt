package org.latin.server.flows

import org.koin.dsl.module

val flowModule = module {

    single { FlowRunner(get()) }
    single<FlowRepository> { InMemoryFlowRepository() }
}
