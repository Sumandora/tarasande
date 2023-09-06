package su.mandora.tarasande.feature.rotation.components

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventPacket
import su.mandora.tarasande.feature.rotation.Rotations

class PreventRotationLeak(rotations: Rotations) {

    init {
        EventDispatcher.add(EventPacket::class.java, 10000) { event ->
            if (event.type == EventPacket.Type.SEND && event.packet is PlayerMoveC2SPacket) {
                rotations.fakeRotation?.also {
                    event.packet.yaw = it.yaw
                    event.packet.pitch = it.pitch
                }
            }
        }
    }

}