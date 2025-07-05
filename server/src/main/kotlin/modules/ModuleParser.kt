package org.latin.server.modules

import org.slf4j.LoggerFactory
import java.io.File

private val log = LoggerFactory.getLogger("ModuleParser")

private val endpointRegex = "<ENDPOINT:(.*?)>".toRegex(RegexOption.IGNORE_CASE)

fun parseModuleFile(file: File): LatinModule {
    val content = file.readText()
    val endpoints = endpointRegex.findAll(content).map { it.groupValues[1] }
        .filter { it.isNotBlank() }
        .toSet()
    val outputSymbols = outputSymbolsRegex.findAll(content).map { it.groupValues[1] }
        .filter { it.isNotBlank() }
        .toSet()
    return LatinModule(
        name = file.nameWithoutExtension,
        version = "1.0.0",
        description = "TODO",
        endpoints = endpoints,
        instructions = content.replace(endpointRegex, "").trim(),
        outputs = outputSymbols.takeIf { it.isNotEmpty() },
    )
}

private val outputSymbolsRegex = "@respond\\s+(\\S+)".toRegex(RegexOption.IGNORE_CASE)
private val quotedOutputSymbolsRegex = "@respond\\s+\"(.+?)\"".toRegex(RegexOption.IGNORE_CASE)

fun String.findOutputSymbols(): Set<String> {
    val outputSymbols = outputSymbolsRegex.findAll(this).map { it.groupValues[1] }
        .filter { it.isNotBlank() }
        .filter { !it.startsWith("\"") } // TODO
        .toSet()
    val quotedOutputSymbols = quotedOutputSymbolsRegex.findAll(this).map { it.groupValues[1] }
        .filter { it.isNotBlank() }
        .toSet()
    return outputSymbols + quotedOutputSymbols
}
