package org.latin.server.modules

import org.eclipse.lmos.arc.agents.ConversationAgent
import org.eclipse.lmos.arc.agents.dsl.extensions.info
import org.eclipse.lmos.arc.agents.dsl.get
import org.eclipse.lmos.arc.core.getOrThrow
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

    suspend fun loadModules(folder: File, agent: ConversationAgent, moduleExecutor: ModuleExecutor) {
        log.info("Loading modules from : ${folder.absoluteFile}")
        val moduleFiles = folder.listFiles() ?: return
        moduleFiles.forEach { moduleFile ->
            if (moduleFile.isFile) {
                log.info("Loading module: ${moduleFile.name}")
                val module = parseModuleFile(moduleFile)
                modules[moduleFile.nameWithoutExtension] = module
                moduleExecutor.runModule(agent, module = module).getOrThrow()
                log.info("Loaded module: $module")
            }
        }
        ready.set(true)
    }

    fun getModuleByEndpoint(endpoint: String): LatinModule? {
        return modules.values.firstOrNull { endpoint in it.triggers }
    }

    fun getModuleByName(name: String): LatinModule? {
        return modules[name]
    }

    fun list(): List<LatinModule> {
        return modules.values.toList()
    }
}
