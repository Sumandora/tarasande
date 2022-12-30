package net.tarasandedevelopment.tarasande_crasher.spigot

object SpigotRules {

    // Spigot login state machine definition since 2016
    fun spigotRule186(input: String): String {
        var input = input.replace("\\.", "")
        return if (input.length > 16) input.substring(0, 16) else input
    }
}
