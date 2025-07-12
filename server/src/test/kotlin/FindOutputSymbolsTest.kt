package org.latin.server

import org.junit.jupiter.api.Test
import org.latin.server.modules.findOutputSymbols

class FindOutputSymbolsTest {

    @Test
    fun `test find output symbols`() {
        val content = """
            @respond symbol1 Some other text
            @respond  symbol2
            Some other text
            @respond symbol3 There are some respond here @respond symbol4
            This is a test content with no symbols
            This is another line with @respond "a long symbol name" cool
        """.trimIndent()

        val foundSymbols = content.findOutputSymbols()

        val expectedSymbols = setOf("symbol1", "symbol2", "symbol3", "symbol4", "a long symbol name")
        assert(foundSymbols == expectedSymbols) { "Expected $expectedSymbols but found $foundSymbols" }
    }
}
