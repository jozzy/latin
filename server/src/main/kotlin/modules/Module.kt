package org.latin.server.modules

import org.koin.dsl.module
import org.latin.server.setup.modulesFolder

val latinModule = module {
    single { ModuleExecutor(get(), get()) }

    single { ModulesManager() }

    single(createdAtStart = true) {
        ConnectEventsToModules(modulesFolder, get(), get(), get()).connect()
    }
}
