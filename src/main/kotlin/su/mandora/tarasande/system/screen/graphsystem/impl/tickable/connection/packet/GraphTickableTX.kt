package su.mandora.tarasande.system.screen.graphsystem.impl.tickable.connection.packet

import su.mandora.tarasande.mc
import su.mandora.tarasande.system.screen.graphsystem.GraphTickable

class GraphTickableTX : GraphTickable("Connection", "TX", 200, true) {

    override fun tick() = mc.networkHandler?.connection?.averagePacketsSent?.toInt()
}

