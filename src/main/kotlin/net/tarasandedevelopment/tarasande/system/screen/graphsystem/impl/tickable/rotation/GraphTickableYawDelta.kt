package net.tarasandedevelopment.tarasande.system.screen.graphsystem.impl.tickable.rotation

import net.minecraft.client.MinecraftClient
import net.tarasandedevelopment.tarasande.event.EventDisconnect
import net.tarasandedevelopment.tarasande.event.EventUpdate
import net.tarasandedevelopment.tarasande.system.screen.graphsystem.Graph
import su.mandora.event.EventDispatcher

class GraphTickableYawDelta : Graph("Yaw Delta", 200, false) {
    private var lastYaw: Float? = null

    init {
        EventDispatcher.apply {
            add(EventUpdate::class.java) { event ->
                if (event.state == EventUpdate.State.POST) {
                    val lastYaw = MinecraftClient.getInstance().player?.lastPitch ?: return@add
                    if(this@GraphTickableYawDelta.lastYaw != null)
                        add(this@GraphTickableYawDelta.lastYaw!! - lastYaw)
                    this@GraphTickableYawDelta.lastYaw = lastYaw
                }
            }
            add(EventDisconnect::class.java) { event ->
                if (event.connection == MinecraftClient.getInstance().networkHandler?.connection) {
                    lastYaw = null
                    clear()
                }
            }
        }
    }
}

