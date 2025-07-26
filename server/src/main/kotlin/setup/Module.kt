package org.latin.server.setup

import org.koin.dsl.module
import reload.ModuleHotReload
import java.io.File

val modulesFolder = File("../modules")

val setupModule = module {

    single(createdAtStart = true) {
        ModuleHotReload(get()).start(modulesFolder)
    }

    single(createdAtStart = true) {
        ConnectEventsToModules(modulesFolder, get(), get(), get()).connect()
    }
}
