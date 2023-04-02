package su.mandora.tarasande.system.screen.graphsystem.impl.tickable

import su.mandora.tarasande.system.screen.graphsystem.GraphTickable
import su.mandora.tarasande.util.string.StringUtil

class GraphTickableMemory : GraphTickable("Game", "Memory", 200, false) {
    override fun tick() = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()

    override fun format(num: Number?): String? {
        return StringUtil.formatBytes(num?.toLong() ?: return null, decimalPlaces)
    }
}