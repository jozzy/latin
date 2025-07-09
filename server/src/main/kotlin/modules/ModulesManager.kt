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
    private var onModulesLoaded: (() -> Unit)? = null

    private val ready = AtomicBoolean(false)
    val isReady: Boolean
        get() = ready.get()

    fun setOnModulesLoaded(callback: () -> Unit) {
        onModulesLoaded = callback
    }

    suspend fun loadModules(folder: File, agent: ConversationAgent, moduleExecutor: ModuleExecutor) {
        log.info("Loading modules from : ${folder.absoluteFile}")
        val moduleFiles = folder.listFiles() ?: return
        moduleFiles.forEach { moduleFile ->
            if (moduleFile.isFile && moduleFile.extension == "latin") {
                log.info("Loading module: ${moduleFile.name}")
                val module = parseModuleFile(moduleFile)
                modules[module.name] = module
                log.info("Loaded module: ${module.name} (UseCase: ${module.useCase})")
                log.info("  - Description: ${module.description ?: "None"}")
                log.info("  - Start condition: ${module.startCondition ?: "None"}")
                log.info("  - Input parameters: ${module.inputParameters}")
                log.info("  - Outputs: ${module.outputs ?: "None"}")
                
                // Execute modules without explicit endpoints or start conditions (legacy behavior)
                if (module.endpoints.isEmpty() && module.startCondition == null) {
                    moduleExecutor.runModule(agent, module = module).getOrThrow()
                }
            }
        }
        ready.set(true)
        log.info("Loaded ${modules.size} modules")
        
        // Trigger callback after modules are loaded
        onModulesLoaded?.invoke()
    }

    fun getModuleByEndpoint(endpoint: String): LatinModule? {
        return modules.values.firstOrNull { endpoint in it.endpoints }
    }

    fun getModuleByName(name: String): LatinModule? {
        return modules[name]
    }

    fun getModuleByUseCase(useCase: String): LatinModule? {
        return modules.values.firstOrNull { it.useCase == useCase }
    }

    fun list(): List<LatinModule> {
        return modules.values.toList()
    }

    fun getModulesWithParameters(): List<LatinModule> {
        return modules.values.filter { it.inputParameters.isNotEmpty() }
    }

    fun getModulesWithStartConditions(): List<LatinModule> {
        return modules.values.filter { it.startCondition != null }
    }
}
