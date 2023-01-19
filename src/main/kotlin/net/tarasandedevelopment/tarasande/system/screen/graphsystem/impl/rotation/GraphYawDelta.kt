package net.tarasandedevelopment.tarasande.system.screen.graphsystem.impl.rotation

import net.tarasandedevelopment.tarasande.event.EventDisconnect
import net.tarasandedevelopment.tarasande.event.EventUpdate
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.screen.graphsystem.Graph
import su.mandora.event.EventDispatcher

class GraphYawDelta : Graph("Player", "Yaw Delta", 200, false) {
    private var lastYaw: Float? = null

    init {
        EventDispatcher.apply {
            add(EventUpdate::class.java) { event ->
                if (event.state == EventUpdate.State.POST) {
                    val lastYaw = mc.player?.lastYaw ?: return@add
                    if (this@GraphYawDelta.lastYaw != null)
                        add(lastYaw - this@GraphYawDelta.lastYaw!!)
                    this@GraphYawDelta.lastYaw = lastYaw
                }
            }
            add(EventDisconnect::class.java) { event ->
                if (event.connection == mc.networkHandler?.connection) {
                    lastYaw = null
                    clear()
                }
            }
        }
    }
}

