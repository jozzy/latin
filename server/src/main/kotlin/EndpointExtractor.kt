package org.latin.server


private val endpointRegex = "<ENDPOINT:(.*?)>".toRegex(RegexOption.IGNORE_CASE)

/**
 * Extracts endpoints from the given input string.
 * Endpoints are expected to be in the format <ENDPOINT:...>.
 *
 * @param input The input string containing endpoints.
 * @return A list of unique endpoint strings extracted from the input.
 */
fun extractEndpoints(input: String): List<String> {
    return endpointRegex.findAll(input)
        .map { it.groupValues[1] }
        .distinct()
        .toList()
}