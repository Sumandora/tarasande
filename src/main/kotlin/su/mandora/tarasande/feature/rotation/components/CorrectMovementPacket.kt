package su.mandora.tarasande.feature.rotation.components

import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventUpdate
import su.mandora.tarasande.feature.rotation.Rotations
import su.mandora.tarasande.feature.rotation.api.Rotation
import su.mandora.tarasande.mc

class CorrectMovementPacket(rotations: Rotations) {

    // We need this because we need to set lastYaw and lastPitch in ClientPlayerEntity to enforce correct packets

    private var cachedRotation: Rotation? = null

    init {
        EventDispatcher.apply {
            add(EventUpdate::class.java, 9999) { event ->
                if (event.state == EventUpdate.State.PRE_PACKET) { // Last one who changes rotation
                    rotations.fakeRotation?.also {
                        cachedRotation = Rotation(mc.player!!)
                        it.applyOn(mc.player!!)
                        return@add
                    }
                    cachedRotation = null
                }
            }
            add(EventUpdate::class.java, 1) { event ->
                if (event.state == EventUpdate.State.POST) { // First one to change it back
                    cachedRotation?.applyOn(mc.player!!)
                }
            }
        }
    }

}