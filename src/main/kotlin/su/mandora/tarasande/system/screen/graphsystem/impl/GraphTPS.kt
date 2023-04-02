package su.mandora.tarasande.system.screen.graphsystem.impl

import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventDisconnect
import su.mandora.tarasande.event.impl.EventPacket
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.screen.graphsystem.Graph

class GraphTPS : Graph("Server", "TPS", 25, false) {

    private var lastWorldTimePacket = 0L
    private var timeDeltas = ArrayList<Long>()

    private val samples = ValueNumber(this, "Samples", 1.0, 2.0, 10.0, 1.0)

    init {
        EventDispatcher.apply {
            add(EventPacket::class.java) {
                if (it.type == EventPacket.Type.RECEIVE && it.packet is WorldTimeUpdateS2CPacket) {
                    if (lastWorldTimePacket > 0L) {
                        timeDeltas.add(System.currentTimeMillis() - lastWorldTimePacket)
                        while (timeDeltas.size > samples.value)
                            timeDeltas.removeAt(0)
                        add(timeDeltas.average() / 1000.0 * 20.0)
                    }
                    lastWorldTimePacket = System.currentTimeMillis()
                }
            }

            add(EventDisconnect::class.java) {
                if (it.connection == mc.networkHandler?.connection) {
                    lastWorldTimePacket = 0L
                    timeDeltas.clear()
                    clear()
                }
            }
        }
    }
}