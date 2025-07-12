package org.latin.server.modules

import org.slf4j.LoggerFactory
import java.io.File

private val log = LoggerFactory.getLogger("ModuleParser")
private val triggersRegex = "@trigger\\s+([^\\s.]+)".toRegex(RegexOption.IGNORE_CASE)
private val outputSymbolsRegex = "@respond\\s+([^\\s.]+)".toRegex(RegexOption.IGNORE_CASE)
private val quotedOutputSymbolsRegex = "@respond\\s+\"(.+?)\"".toRegex(RegexOption.IGNORE_CASE)
private val linkRegex = "#([^\\s.]+)".toRegex(RegexOption.IGNORE_CASE)

private val toolsRegex = "@tool\\s+([^\\s.]+)".toRegex(RegexOption.IGNORE_CASE)

private val keywords = setOf("tool", "respond")

/**
 * Parses a module file and creates a `LatinModule` object.
 *
 * Reads the content of the given file, extracts endpoints, output symbols, and handover instructions,
 * and returns a corresponding `LatinModule` with the extracted information.
 *
 * @param file The module file to parse.
 * @return The created `LatinModule` object with the extracted data.
 */
fun parseModuleFile(file: File): LatinModule = parseModule(file.nameWithoutExtension, file.readText())
fun parseModule(name: String, content: String): LatinModule {
    return LatinModule(
        name = name,
        version = "1.0.0",
        description = content.extractTripleSlashLines(),
        triggers = triggersRegex.extractFrom(content, filter = keywords),
        instructions = content.removeComments().trim(),
        outputs = content.findOutputSymbols().takeIf { it.isNotEmpty() },
        handovers = linkRegex.extractFrom(content),
        tools = toolsRegex.extractFrom(content),
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

private fun Regex.extractFrom(content: String, filter: Set<String> = emptySet()): Set<String> {
    return findAll(content).map { it.groupValues[1] }
        .filter { it.isNotBlank() }
        .filter { !filter.contains(it) }
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
