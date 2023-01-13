package net.tarasandedevelopment.tarasande.system.screen.graphsystem.impl.tickable.connection.packet

import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.screen.graphsystem.GraphTickable

class GraphTickableRX : GraphTickable("Connection", "RX", 200, true) {

    override fun tick(): Number? {
        if (mc.world == null) return null

        return mc.networkHandler!!.connection.averagePacketsReceived.toInt()
    }
}