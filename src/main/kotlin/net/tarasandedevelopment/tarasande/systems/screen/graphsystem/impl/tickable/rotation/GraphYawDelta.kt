package net.tarasandedevelopment.tarasande.systems.screen.graphsystem.impl.tickable.rotation

import net.minecraft.client.MinecraftClient
import net.tarasandedevelopment.tarasande.event.EventUpdate
import net.tarasandedevelopment.tarasande.systems.screen.graphsystem.Graph
import su.mandora.events.EventDispatcher

class GraphYawDelta : Graph("Yaw Delta", 200, false) {
    private var lastYaw = 0.0f

    init {
        EventDispatcher.add(EventUpdate::class.java) { event ->
            if (event.state == EventUpdate.State.POST) {
                val lastYaw = MinecraftClient.getInstance().player?.lastPitch ?: return@add
                add(this.lastYaw - lastYaw)
                this.lastYaw = lastYaw
            }
        }
    }
}

