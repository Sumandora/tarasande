package net.tarasandedevelopment.tarasande.systems.screen.graphsystem.impl.tickable

import net.tarasandedevelopment.tarasande.systems.screen.graphsystem.GraphTickable
import net.tarasandedevelopment.tarasande.util.string.StringUtil

class GraphMemory : GraphTickable("Memory", 200, false) {
    override fun tick() = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()

    override fun format(num: Number?): String? {
        return StringUtil.formatBytes(num?.toLong() ?: return null, decimalPlaces)
    }
}