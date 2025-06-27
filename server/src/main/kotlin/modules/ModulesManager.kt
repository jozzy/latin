package org.latin.server.modules

import org.slf4j.LoggerFactory
import java.io.File
import java.util.concurrent.ConcurrentHashMap

class ModulesManager {

    private val log = LoggerFactory.getLogger(ModulesManager::class.java)

    private val modules = ConcurrentHashMap<String, LatinModule>()

    fun loadModules(folder: File) {
        log.info("Loading modules from : ${folder.absoluteFile}")
        val moduleFiles = folder.listFiles() ?: return
        moduleFiles.forEach { moduleFile ->
            if (moduleFile.isFile) {
                log.info("Loading module: ${moduleFile.name}")
                val module = parseModuleFile(moduleFile)
                modules[moduleFile.name] = module
                log.info("Loaded module: $module")
            }
        }
    }

    fun getModuleByEndpoint(endpoint: String): LatinModule? {
        return modules.values.firstOrNull { endpoint in it.endpoints }
    }
}