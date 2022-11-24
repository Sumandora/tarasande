package net.tarasandedevelopment.tarasande.systems.screen.graphsystem.impl

import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket
import net.tarasandedevelopment.tarasande.event.EventDisconnect
import net.tarasandedevelopment.tarasande.event.EventPacket
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.systems.screen.graphsystem.Graph
import su.mandora.event.EventDispatcher
import kotlin.math.round

class GraphTPS : Graph("TPS", 10, false) {

    /* Explanation:
     * A WorldTimeUpdate Packet is sent every 20 ticks (a second)
     * Now we take the time from the last to the current packet
     * and divide by 1000ms that would be the correct answer if the tps would be 1 by default
     * but of course this is wrong means we have to multiply by 20
     * now we got the tps
     * in case the server is a 60 tps server (gamster moment)
     * the packets would come trice as fast
     * resulting in the timeDelta not being 1000ms but 333ms means it is trice the amount
     *
     * Conclusion: it is correct
     */

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
                        add(round(timeDeltas.average() / 1000.0 * 20 * 100) / 100.0)
                    }
                    lastWorldTimePacket = System.currentTimeMillis()
                }
            }

            add(EventDisconnect::class.java) {
                lastWorldTimePacket = 0L
                timeDeltas.clear()
            }
        }
    }
}