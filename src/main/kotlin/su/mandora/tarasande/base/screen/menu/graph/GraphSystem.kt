package su.mandora.tarasande.base.screen.menu.graph

import de.florianmichael.tarasande.screen.menu.graph.GraphIncomingTraffic
import de.florianmichael.tarasande.screen.menu.graph.GraphOutgoingTraffic
import su.mandora.tarasande.base.Manager
import su.mandora.tarasande.screen.menu.graph.*

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
}
