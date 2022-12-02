package net.tarasandedevelopment.tarasande.system.screen.graphsystem.impl.tickable.connection.packet

import net.minecraft.client.MinecraftClient
import net.tarasandedevelopment.tarasande.system.screen.graphsystem.GraphTickable

class GraphTickableTX : GraphTickable("Connection", "TX", 200, true) {

    override fun tick(): Number? {
        if (MinecraftClient.getInstance().world == null) return null

        return MinecraftClient.getInstance().networkHandler!!.connection.averagePacketsSent.toInt()
    }
}

