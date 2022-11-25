package net.tarasandedevelopment.tarasande.system.screen.graphsystem.impl.tickable.rotation

import net.minecraft.client.MinecraftClient
import net.tarasandedevelopment.tarasande.event.EventUpdate
import net.tarasandedevelopment.tarasande.system.screen.graphsystem.Graph
import su.mandora.event.EventDispatcher

class GraphTickablePitchDelta : Graph("Pitch Delta", 200, false) {
    private var lastPitch = 0.0F

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