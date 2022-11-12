package net.tarasandedevelopment.tarasande.systems.screen.graphsystem

import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.Manager
import net.tarasandedevelopment.events.impl.EventTick
import net.tarasandedevelopment.tarasande.systems.screen.graphsystem.impl.*
import net.tarasandedevelopment.tarasande.systems.screen.graphsystem.information.InformationGraphValue
import net.tarasandedevelopment.tarasande.systems.screen.graphsystem.panel.PanelGraph

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
        TarasandeMain.get().panelSystem.add(*list.map { e -> PanelGraph(e) }.toTypedArray())

        TarasandeMain.get().eventSystem.add(EventTick::class.java) {
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
