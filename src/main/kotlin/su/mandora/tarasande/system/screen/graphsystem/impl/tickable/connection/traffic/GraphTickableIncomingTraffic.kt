package su.mandora.tarasande.system.screen.graphsystem.impl.tickable.connection.traffic

import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventPacketTransform
import su.mandora.tarasande.system.screen.graphsystem.GraphTickable
import su.mandora.tarasande.util.string.StringUtil
import java.util.concurrent.CopyOnWriteArrayList

class GraphTickableIncomingTraffic : GraphTickable("Connection", "Incoming Traffic", 200, true) {

    private val traffic = CopyOnWriteArrayList<Pair<Long, Int>>()

    init {
        EventDispatcher.add(EventPacketTransform::class.java) {
            if (it.type == EventPacketTransform.Type.DECODE) {
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