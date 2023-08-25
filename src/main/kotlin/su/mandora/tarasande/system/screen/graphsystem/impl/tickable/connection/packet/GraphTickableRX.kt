package su.mandora.tarasande.system.screen.graphsystem.impl.tickable.connection.packet

import su.mandora.tarasande.mc
import su.mandora.tarasande.system.screen.graphsystem.GraphTickable

class GraphTickableRX : GraphTickable("Connection", "RX", 200, true) {

    override fun tick() = mc.networkHandler?.connection?.averagePacketsReceived?.toInt()
}