package su.mandora.tarasande.feature.rotation

import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventUpdate
import su.mandora.tarasande.mc
import su.mandora.tarasande.util.math.rotation.Rotation

class PacketRotator(rotations: Rotations) {

    private var cachedRotation: Rotation? = null

    init {
        EventDispatcher.apply {
            add(EventUpdate::class.java, 9999) { event ->
                if (event.state == EventUpdate.State.PRE_PACKET) { // Last one who changes rotation
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
                if (event.state == EventUpdate.State.POST) { // First one to change it back
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