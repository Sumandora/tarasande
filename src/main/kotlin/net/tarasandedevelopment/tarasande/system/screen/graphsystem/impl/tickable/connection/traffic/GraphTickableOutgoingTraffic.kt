package net.tarasandedevelopment.tarasande.system.screen.graphsystem.impl.tickable.connection.traffic

import net.tarasandedevelopment.tarasande.event.EventPacketTransform
import net.tarasandedevelopment.tarasande.system.screen.graphsystem.GraphTickable
import net.tarasandedevelopment.tarasande.util.string.StringUtil
import su.mandora.event.EventDispatcher
import java.util.concurrent.CopyOnWriteArrayList

class GraphTickableOutgoingTraffic : GraphTickable("Outgoing Traffic", 200, true) {

    private val traffic = CopyOnWriteArrayList<Pair<Long, Int>>()

    init {
        EventDispatcher.add(EventPacketTransform::class.java) {
            if (it.type == EventPacketTransform.Type.ENCODE) {
                traffic.add(Pair(System.currentTimeMillis(), it.buf!!.readableBytes()))
            }
        }
    }

    override fun tick(): Number {
        traffic.removeIf { traffic -> System.currentTimeMillis() - traffic.first > 1000 }
        return traffic.sumOf { it.second }
    }

    override fun format(num: Number?): String? {
        return StringUtil.formatBytes(num?.toLong() ?: return null, decimalPlaces)
    }
}