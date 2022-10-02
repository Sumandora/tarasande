package net.tarasandedevelopment.tarasande.base.screen.menu.graph

import net.tarasandedevelopment.tarasande.base.Manager
import net.tarasandedevelopment.tarasande.screen.menu.graph.*

class ManagerGraph : Manager<Graph>() {

    init {
        add(
            GraphFPS(),
            GraphTPS(),
            GraphCPS(),
            GraphYawDelta(),
            GraphPitchDelta(),
            GraphMotion(),
            GraphPing(),
            GraphOnlinePlayers(),
            GraphMemory(),
            GraphIncomingTraffic(),
            GraphOutgoingTraffic()
        )
    }
}

abstract class Graph(val name: String, val bufferLength: Int) {
    abstract fun supplyData(): Number?

    open fun formatHud() = this.supplyData().toString()
}
