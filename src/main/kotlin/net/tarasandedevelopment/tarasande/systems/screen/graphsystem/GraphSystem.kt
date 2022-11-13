package net.tarasandedevelopment.tarasande.systems.screen.graphsystem

import su.mandora.event.EventDispatcher
import net.tarasandedevelopment.tarasande.Manager
import net.tarasandedevelopment.tarasande.event.EventTick
import net.tarasandedevelopment.tarasande.systems.screen.graphsystem.impl.*
import net.tarasandedevelopment.tarasande.systems.screen.graphsystem.information.InformationGraphValue
import net.tarasandedevelopment.tarasande.systems.screen.graphsystem.panel.PanelGraph
import net.tarasandedevelopment.tarasande.systems.screen.informationsystem.ManagerInformation
import net.tarasandedevelopment.tarasande.systems.screen.panelsystem.ManagerPanel

class ManagerGraph(informationSystem: ManagerInformation, panelSystem: ManagerPanel) : Manager<Graph>() {

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

        informationSystem.add(*list.map { e -> InformationGraphValue(e) }.toTypedArray())
        panelSystem.add(*list.map { e -> PanelGraph(e) }.toTypedArray())

        EventDispatcher.add(EventTick::class.java) {
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
