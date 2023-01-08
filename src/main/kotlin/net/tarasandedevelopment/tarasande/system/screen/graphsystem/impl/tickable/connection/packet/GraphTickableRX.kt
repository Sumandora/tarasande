package net.tarasandedevelopment.tarasande.system.screen.graphsystem.impl.tickable.connection.packet

import net.tarasandedevelopment.tarasande.system.screen.graphsystem.GraphTickable
import net.tarasandedevelopment.tarasande.util.extension.mc

class GraphTickableRX : GraphTickable("Connection", "RX", 200, true) {

    override fun tick(): Number? {
        if (mc.world == null) return null

        return mc.networkHandler!!.connection.averagePacketsReceived.toInt()
    }
}