package org.latin.server.modules

import org.slf4j.LoggerFactory
import java.io.File

private val log = LoggerFactory.getLogger("ModuleParser")

// Legacy endpoint regex for backward compatibility
private val endpointRegex = "<ENDPOINT:(.*?)>".toRegex(RegexOption.IGNORE_CASE)

// New UseCase syntax regexes
private val useCaseRegex = "UseCase:\\s*(.+?)(?:\\n|$)".toRegex(RegexOption.IGNORE_CASE)
private val startWhenRegex = "Start when (.+?)(?:\\n|:)".toRegex(RegexOption.IGNORE_CASE)
private val endBlockRegex = "End\\.?\\s*$".toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE))
private val descriptionRegex = "Description:\\s*(.+?)(?:\\n|$)".toRegex(RegexOption.IGNORE_CASE)

// Parameter extraction patterns
private val parameterPattern = "\\b(\\w+)\\b(?:\\s+and\\s+\\b(\\w+)\\b)*".toRegex()
private val receivedPattern = "(?:are\\s+)?received".toRegex(RegexOption.IGNORE_CASE)

fun parseModuleFile(file: File): LatinModule {
    val content = file.readText()
    
    // Check if it's a new UseCase format or legacy format
    val isNewFormat = content.contains("UseCase:", ignoreCase = true)
    
    if (isNewFormat) {
        return parseNewFormatModule(file, content)
    } else {
        return parseLegacyFormatModule(file, content)
    }
}

private fun parseNewFormatModule(file: File, content: String): LatinModule {
    log.info("Parsing new format module: ${file.name}")
    
    // Extract UseCase name
    val useCase = useCaseRegex.find(content)?.groupValues?.get(1)?.trim()
        ?: file.nameWithoutExtension
    
    // Extract description
    val description = descriptionRegex.find(content)?.groupValues?.get(1)?.trim()
    
    // Extract start condition and parameters
    val startCondition = startWhenRegex.find(content)?.groupValues?.get(1)?.trim()
    val inputParameters = extractParametersFromStartCondition(startCondition)
    
    // Extract instructions (content between Start and End)
    val instructions = extractInstructions(content)
    
    // Extract output symbols
    val outputSymbols = content.findOutputSymbols()
    
    // Extract channels (future enhancement)
    val channels = setOf("http") // Default for now
    
    return LatinModule(
        name = file.nameWithoutExtension,
        useCase = useCase,
        description = description,
        version = "1.0.0",
        startCondition = startCondition,
        inputParameters = inputParameters,
        instructions = instructions,
        outputs = outputSymbols.takeIf { it.isNotEmpty() },
        channels = channels
    )
}

private fun parseLegacyFormatModule(file: File, content: String): LatinModule {
    log.info("Parsing legacy format module: ${file.name}")
    
    // Legacy parsing logic
    val endpoints = endpointRegex.findAll(content).map { it.groupValues[1] }
        .filter { it.isNotBlank() }
        .toSet()
    
    val outputSymbols = content.findOutputSymbols()
    
    return LatinModule(
        name = file.nameWithoutExtension,
        useCase = file.nameWithoutExtension,
        description = "Legacy module",
        version = "1.0.0",
        startCondition = null,
        inputParameters = emptySet(),
        instructions = content.replace(endpointRegex, "").trim(),
        outputs = outputSymbols.takeIf { it.isNotEmpty() },
        channels = setOf("http"),
        endpoints = endpoints
    )
}

private fun extractParametersFromStartCondition(startCondition: String?): Set<String> {
    if (startCondition == null) return emptySet()
    
    // Remove "are received" or "received" part
    val cleanCondition = startCondition.replace(receivedPattern, "").trim()
    
    // Extract parameters using pattern matching
    val parameters = mutableSetOf<String>()
    
    // Handle patterns like "recipient and email_body are received"
    val words = cleanCondition.split("\\s+".toRegex())
    for (word in words) {
        if (word.matches("\\w+".toRegex()) && 
            !word.equals("and", ignoreCase = true) && 
            !word.equals("are", ignoreCase = true) &&
            !word.equals("when", ignoreCase = true)) {
            parameters.add(word)
        }
    }
    
    return parameters
}

private fun extractInstructions(content: String): String {
    // Extract content between "Start when" and "End"
    val startIndex = content.indexOf("Start when", ignoreCase = true)
    val endIndex = content.lastIndexOf("End.", ignoreCase = true)
    
    if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
        // Find the end of the "Start when" line
        val startLineEnd = content.indexOf('\n', startIndex)
        if (startLineEnd != -1) {
            return content.substring(startLineEnd + 1, endIndex).trim()
        }
    }
    
    // Fallback: return entire content
    return content.trim()
}

// Enhanced output symbols parsing
private val outputSymbolsRegex = "@respond\\s+(\\S+)".toRegex(RegexOption.IGNORE_CASE)
private val quotedOutputSymbolsRegex = "@respond\\s+\"(.+?)\"".toRegex(RegexOption.IGNORE_CASE)
private val withOutputSymbolsRegex = "@respond\\s+with\\s+(\\S+)".toRegex(RegexOption.IGNORE_CASE)

fun String.findOutputSymbols(): Set<String> {
    val outputSymbols = outputSymbolsRegex.findAll(this).map { it.groupValues[1] }
        .filter { it.isNotBlank() }
        .filter { !it.startsWith("\"") }
        .toSet()
    
    val quotedOutputSymbols = quotedOutputSymbolsRegex.findAll(this).map { it.groupValues[1] }
        .filter { it.isNotBlank() }
        .toSet()
    
    val withOutputSymbols = withOutputSymbolsRegex.findAll(this).map { it.groupValues[1] }
        .filter { it.isNotBlank() }
        .toSet()
    
    return outputSymbols + quotedOutputSymbols + withOutputSymbols
}
