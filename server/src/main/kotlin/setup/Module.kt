package org.latin.server.setup

import org.koin.dsl.module
import reload.ModuleHotReload
import java.io.File

val modulesFolder = File("../modules")

val setupModule = module {

    single(createdAtStart = true) {
        ModuleHotReload().start(modulesFolder)
    }

    single(createdAtStart = true) {
        ConnectEventsToModules(modulesFolder).connect()
    }
}
