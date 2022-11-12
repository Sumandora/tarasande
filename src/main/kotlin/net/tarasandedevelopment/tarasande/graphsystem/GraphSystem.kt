package net.tarasandedevelopment.tarasande.graphsystem

import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.Manager
import net.tarasandedevelopment.tarasande.event.EventTick
import net.tarasandedevelopment.tarasande.graphsystem.impl.*
import net.tarasandedevelopment.tarasande.graphsystem.information.InformationGraphValue

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

        TarasandeMain.get().informationSystem.add(*list.map { e -> InformationGraphValue(e) }.toTypedArray())

        TarasandeMain.get().managerEvent.add(EventTick::class.java) {
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
