package su.mandora.tarasande.system.screen.graphsystem.impl.rotation

import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventDisconnect
import su.mandora.tarasande.event.impl.EventUpdate
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.screen.graphsystem.Graph

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

