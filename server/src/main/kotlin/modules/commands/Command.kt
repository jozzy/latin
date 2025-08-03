package org.latin.server.modules.commands

interface Command {
    fun matches(line: String): Boolean
}
