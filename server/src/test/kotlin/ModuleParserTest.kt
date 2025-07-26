package org.latin.server

import org.junit.jupiter.api.Test
import org.latin.server.modules.parseModule

class ModuleParserTest {

    @Test
    fun `test greeting module`() {
        val m = parseModule(
            "greeting",
            """
           /// My first module 
           
           @trigger greeting
           
           @instructions
           Respond with a greeting message.
           
           @output
           Return a friendly greeting message.
           
        """,
        )
        println(m)
    }

    @Test
    fun `test scheduled module`() {
        val m = parseModule(
            "greeting",
            """
           /// My first module 
           
           @once 10 
           
           @instructions
           Respond with a greeting message.
           
           @output
           Return a friendly greeting message.
           
        """,
        )
        println(m)
    }
}
