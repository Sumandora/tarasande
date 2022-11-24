package net.tarasandedevelopment.tarasande.systems.screen.graphsystem.impl.tickable.connection.packets

import net.minecraft.client.MinecraftClient
import net.tarasandedevelopment.tarasande.systems.screen.graphsystem.GraphTickable

class GraphTX : GraphTickable("TX", 200, true) {

    override fun tick(): Number? {
        if (MinecraftClient.getInstance().world == null) return null

        return MinecraftClient.getInstance().networkHandler!!.connection.averagePacketsSent.toInt()
    }
}

