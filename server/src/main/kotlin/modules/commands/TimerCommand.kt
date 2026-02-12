package org.latin.server.modules.commands

import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.toDuration

class TimerCommand : Command {

    private val regex = "@every\\s+([\\d.]+)\\s+(seconds|minutes)".toRegex(RegexOption.IGNORE_CASE)

    var timer: Timer? = null

    fun get(): Timer? = timer

    override fun matches(line: String): Boolean {
        if (timer != null) return false
        timer = regex.find(line)?.let { match ->
            val (_, timer, unit) = match.groupValues
            Timer(
                timer.toInt().toDuration(
                    when (unit) {
                        "seconds" -> kotlin.time.DurationUnit.SECONDS
                        "minutes" -> kotlin.time.DurationUnit.MINUTES
                        else -> throw IllegalArgumentException("Unknown time unit: $unit")
                    },
                ),
            )
        }
        return timer != null
    }
}

@Serializable
data class Timer(val interval: Duration)
