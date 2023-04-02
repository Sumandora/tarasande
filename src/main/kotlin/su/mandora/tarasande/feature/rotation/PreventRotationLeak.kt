package su.mandora.tarasande.feature.rotation

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventPacket
import su.mandora.tarasande.util.extension.minecraft.packet.evaluateNewRotation

class PreventRotationLeak(rotations: Rotations) {

    private var disableNext = false
    init {
        EventDispatcher.apply {
            add(EventPacket::class.java, 9999) {
                if (it.type == EventPacket.Type.RECEIVE && it.packet is PlayerPositionLookS2CPacket) {
                    disableNext = true
                    if (rotations.fakeRotation != null)
                        rotations.fakeRotation = it.packet.evaluateNewRotation()
                } else if (it.type == EventPacket.Type.SEND && it.packet is PlayerMoveC2SPacket) {
                    if (rotations.fakeRotation != null) {
                        if (disableNext) { // this code is crap ._.
                            disableNext = false
                            return@add
                        }
                        val fakeRotation = rotations.fakeRotation ?: return@add
                        it.packet.yaw = fakeRotation.yaw
                        it.packet.pitch = fakeRotation.pitch
                    }
                }
            }
        }
    }

}