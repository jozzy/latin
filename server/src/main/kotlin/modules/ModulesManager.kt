package org.latin.server.modules

import org.slf4j.LoggerFactory
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

class ModulesManager {

    private val log = LoggerFactory.getLogger(ModulesManager::class.java)

    private val modules = ConcurrentHashMap<String, LatinModule>()

    private val ready = AtomicBoolean(false)
    val isReady: Boolean
        get() = ready.get()

    suspend fun loadModules(folder: File) {
        log.info("Loading modules from : ${folder.absoluteFile}")
        val moduleFiles = folder.listFiles() ?: return
        moduleFiles.forEach { moduleFile ->
            if (moduleFile.isFile) loadModule(moduleFile)
        }
        ready.set(true)
    }

    fun loadModule(moduleFile: File) {
        log.info("Loading module: ${moduleFile.name}")
        val module = parseModuleFile(moduleFile)
        modules[moduleFile.nameWithoutExtension] = module
        log.debug("Loaded module: $module")
    }

    fun getModuleById(id: String): LatinModule? {
        return modules[id]
    }

    fun getByName(name: String): LatinModule? {
        return modules[name]
    }

    fun list(): List<LatinModule> {
        return modules.values.toList()
    }

    fun store(module: LatinModule) {
        modules.put(module.name, module)
    }
}
