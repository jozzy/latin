package org.latin.server.modules

import org.koin.dsl.module

val latinModule = module {
    single { ModuleExecutor(get(), get()) }
    single { ModulesManager() }
}
