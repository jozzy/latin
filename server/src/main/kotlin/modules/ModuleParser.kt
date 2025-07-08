package org.latin.server.modules

import org.slf4j.LoggerFactory
import java.io.File

private val log = LoggerFactory.getLogger("ModuleParser")
private val endpointRegex = "<ENDPOINT:(.*?)>".toRegex(RegexOption.IGNORE_CASE)
private val outputSymbolsRegex = "@respond\\s+([^\\s.]+)".toRegex(RegexOption.IGNORE_CASE)
private val quotedOutputSymbolsRegex = "@respond\\s+\"(.+?)\"".toRegex(RegexOption.IGNORE_CASE)
private val handoverRegex = "#([^\\s.]+)".toRegex(RegexOption.IGNORE_CASE)

/**
 * Parses a module file and creates a `LatinModule` object.
 *
 * Reads the content of the given file, extracts endpoints, output symbols, and handover instructions,
 * and returns a corresponding `LatinModule` with the extracted information.
 *
 * @param file The module file to parse.
 * @return The created `LatinModule` object with the extracted data.
 */
fun parseModuleFile(file: File): LatinModule {
    val content = file.readText()
    val endpoints = endpointRegex.findAll(content).map { it.groupValues[1] }
        .filter { it.isNotBlank() }
        .toSet()
    return LatinModule(
        name = file.nameWithoutExtension,
        version = "1.0.0",
        description = content.extractTripleSlashLines(),
        endpoints = endpoints,
        instructions = content.replace(endpointRegex, "").removeComments().trim(),
        outputs = content.findOutputSymbols().takeIf { it.isNotEmpty() },
        handovers = content.extractHandovers(),
    )
}

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

fun String.extractHandovers(): Set<String> {
    return handoverRegex.findAll(this).map { it.groupValues[1] }
        .filter { it.isNotBlank() }
        .toSet()
}

/**
 * Extracts all lines starting with '///', removes the prefix, and returns the text.
 *
 * @return A string with all extracted lines, each without the '///' prefix, joined by line breaks.
 */
fun String.extractTripleSlashLines(): String = lines()
    .filter { it.trimStart().startsWith("///") }
    .joinToString("\n") { it.trimStart().removePrefix("///").trimStart() }

fun String.removeComments(): String = lines()
    .filter { !it.trimStart().startsWith("//") }
    .joinToString("\n")