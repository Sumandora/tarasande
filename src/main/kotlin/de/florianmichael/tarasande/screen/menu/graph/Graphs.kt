package de.florianmichael.tarasande.screen.menu.graph

import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.base.screen.menu.graph.Graph
import su.mandora.tarasande.event.EventPacketTransform
import java.util.concurrent.CopyOnWriteArrayList

class GraphIncomingTraffic : Graph("Incoming Traffic", 200) {

    private val traffic = CopyOnWriteArrayList<Pair<Long, Int>>()

    init {
        TarasandeMain.get().managerEvent.add { event ->
            if (event is EventPacketTransform && event.type == EventPacketTransform.Type.DECODE) {
                traffic.add(Pair(System.currentTimeMillis(), event.buf!!.readableBytes()))
            }
        }
    }

    override fun supplyData(): Number {
        traffic.removeIf { traffic -> System.currentTimeMillis() - traffic.first > 1000 }
        return traffic.sumOf {
            it.second
        }
    }
}

class GraphOutgoingTraffic : Graph("Outgoing Traffic", 200) {

    private val traffic = CopyOnWriteArrayList<Pair<Long, Int>>()

    init {
        TarasandeMain.get().managerEvent.add { event ->
            if (event is EventPacketTransform && event.type == EventPacketTransform.Type.ENCODE) {
                traffic.add(Pair(System.currentTimeMillis(), event.buf!!.readableBytes()))
            }
        }
    }

    override fun supplyData(): Number {
        traffic.removeIf { traffic -> System.currentTimeMillis() - traffic.first > 1000 }
        return traffic.sumOf {
            it.second
        }
    }
}
