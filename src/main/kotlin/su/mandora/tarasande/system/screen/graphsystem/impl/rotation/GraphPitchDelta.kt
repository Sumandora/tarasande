package su.mandora.tarasande.system.screen.graphsystem.impl.rotation

import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventDisconnect
import su.mandora.tarasande.event.impl.EventUpdate
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.screen.graphsystem.Graph

class GraphPitchDelta : Graph("Player", "Pitch Delta", 200, false) {
    private var lastPitch: Float? = null

    init {
        EventDispatcher.apply {
            add(EventUpdate::class.java) { event ->
                if (event.state == EventUpdate.State.POST) {
                    val lastPitch = mc.player?.lastPitch ?: return@add
                    if (this@GraphPitchDelta.lastPitch != null)
                        add(lastPitch - this@GraphPitchDelta.lastPitch!!)
                    this@GraphPitchDelta.lastPitch = lastPitch
                }
            }
            add(EventDisconnect::class.java) { event ->
                if (event.connection == mc.networkHandler?.connection) {
                    lastPitch = null
                    clear()
                }
            }
        }
    }
}