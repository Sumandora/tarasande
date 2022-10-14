package net.tarasandedevelopment.tarasande.base.screen.cheatmenu.graph

import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.Manager
import net.tarasandedevelopment.tarasande.event.EventTick
import net.tarasandedevelopment.tarasande.screen.cheatmenu.graph.*

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
            GraphOutgoingTraffic(),
            GraphTX(),
            GraphRX()
        )

        TarasandeMain.get().eventDispatcher.add(EventTick::class.java) {
            if (it.state == EventTick.State.PRE)
                for (graph in list)
                    graph.lastData = graph.supplyData()
        }
    }
}

abstract class Graph(val name: String, val bufferLength: Int) {
    var lastData: Number? = null

    abstract fun supplyData(): Number?

    open fun formatHud(): String? = lastData.toString()
}
