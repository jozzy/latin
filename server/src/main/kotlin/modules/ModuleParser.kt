package org.latin.server.modules

import org.latin.server.modules.commands.TimerCommand
import org.slf4j.LoggerFactory
import java.io.File

private val log = LoggerFactory.getLogger("ModuleParser")
private val triggersRegex = "@trigger\\s+([^\\s.]+)".toRegex(RegexOption.IGNORE_CASE)
private val outputSymbolsRegex = "@respond\\s+([^\\s.]+)".toRegex(RegexOption.IGNORE_CASE)
private val quotedOutputSymbolsRegex = "@respond\\s+\"(.+?)\"".toRegex(RegexOption.IGNORE_CASE)
private val linkRegex = "#([^\\s.]+)".toRegex(RegexOption.IGNORE_CASE)

private val toolsRegex = "@tool\\s+([A-Za-z0-9_-]+)".toRegex(RegexOption.IGNORE_CASE)

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

fun String.extractTools(): Set<String> = toolsRegex.extractFrom(this)

fun String.extractHandovers(): Set<String> = linkRegex.extractFrom(this)

fun parseModule(name: String, content: String): LatinModule {
    var description = ""
    var instructions = ""
    var inputTemplate = ""
    var output = ""
    val outputSymbols = mutableSetOf<String>()
    val triggers = mutableSetOf<String>()
    var mode = ReadMode.NONE
    val timerCommand = TimerCommand()

    content.split("\n").forEach {
        val line = it.trim()

        if (line.startsWith("//")) return@forEach

        if (line.startsWith("///")) {
            description += line.removePrefix("//").trim() + "\n"
        } else if (line.startsWith("@trigger")) {
            triggers += triggersRegex.extractFrom(line)
        } else if (line.startsWith("@instructions")) {
            mode = ReadMode.INSTRUCTIONS
        } else if (timerCommand.matches(line)) {
            mode = ReadMode.NONE
        } else if (line.startsWith("@output")) {
            mode = ReadMode.OUTPUT
        } else if (line.startsWith("@input_template")) {
            mode = ReadMode.INPUT_TEMPLATE
        } else {
            when (mode) {
                ReadMode.INSTRUCTIONS -> instructions += "$line\n"
                ReadMode.INPUT_TEMPLATE -> inputTemplate += "$line\n"
                ReadMode.OUTPUT -> {
                    output += "$line\n"
                    outputSymbols += line.findOutputSymbols()
                }

                else -> {
                    // TODO
                }
            }
        }
    }

    return LatinModule(
        name = name,
        version = "1.0.0",
        description = description,
        triggers = triggers,
        instructions = instructions,
        output = output,
        timer = timerCommand.get(),
        outputSymbols = outputSymbols + instructions.findOutputSymbols(),
        handovers = linkRegex.extractFrom(content),
        tools = toolsRegex.extractFrom(content),
        inputTemplate = inputTemplate,
    )
}

enum class ReadMode {
    INSTRUCTIONS,
    OUTPUT,
    INPUT_TEMPLATE,
    VERIFICATION,
    NONE,
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
