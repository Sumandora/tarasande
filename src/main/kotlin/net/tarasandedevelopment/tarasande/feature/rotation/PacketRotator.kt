package net.tarasandedevelopment.tarasande.feature.rotation

import net.tarasandedevelopment.tarasande.event.EventUpdate
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.util.math.rotation.Rotation
import su.mandora.event.EventDispatcher

class PacketRotator(rotations: Rotations) {

    private var cachedRotation: Rotation? = null
    init {
        EventDispatcher.apply {
            add(EventUpdate::class.java, 9999) { event ->
                if(event.state == EventUpdate.State.PRE_PACKET) { // Last one who changes rotation
                    cachedRotation = Rotation(mc.player!!)
                    if (rotations.fakeRotation != null) {
                        mc.player!!.apply {
                            rotations.fakeRotation!!.also {
                                yaw = it.yaw
                                pitch = it.pitch
                            }
                        }
                    }
                }
            }
            add(EventUpdate::class.java, 1) { event ->
                if(event.state == EventUpdate.State.POST) { // First one to change it back
                    mc.player!!.apply {
                        cachedRotation!!.also {
                            yaw = it.yaw
                            pitch = it.pitch
                        }
                    }
                }
            }
        }
    }

}