package net.tarasandedevelopment.tarasande.systems.screen.graphsystem.impl.tickable.rotation

import net.minecraft.client.MinecraftClient
import net.tarasandedevelopment.tarasande.event.EventUpdate
import net.tarasandedevelopment.tarasande.systems.screen.graphsystem.Graph
import su.mandora.event.EventDispatcher

class GraphPitchDelta : Graph("Pitch Delta", 200, false) {
    private var lastPitch = 0.0f

    init {
        EventDispatcher.add(EventUpdate::class.java) { event ->
            if (event.state == EventUpdate.State.POST) {
                val lastPitch = MinecraftClient.getInstance().player?.lastPitch ?: return@add
                add(this.lastPitch - lastPitch)
                this.lastPitch = lastPitch
            }
        }
    }
}