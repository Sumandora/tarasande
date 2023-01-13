package net.tarasandedevelopment.tarasande.system.screen.graphsystem.impl.tickable.rotation

import net.tarasandedevelopment.tarasande.event.EventDisconnect
import net.tarasandedevelopment.tarasande.event.EventUpdate
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.screen.graphsystem.Graph
import su.mandora.event.EventDispatcher

class GraphTickablePitchDelta : Graph("Player", "Pitch Delta", 200, false) {
    private var lastPitch: Float? = null

    init {
        EventDispatcher.apply {
            add(EventUpdate::class.java) { event ->
                if (event.state == EventUpdate.State.POST) {
                    val lastPitch = mc.player?.lastPitch ?: return@add
                    if (this@GraphTickablePitchDelta.lastPitch != null)
                        add(this@GraphTickablePitchDelta.lastPitch!! - lastPitch)
                    this@GraphTickablePitchDelta.lastPitch = lastPitch
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