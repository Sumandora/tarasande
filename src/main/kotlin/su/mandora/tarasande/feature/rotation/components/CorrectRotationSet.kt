package su.mandora.tarasande.feature.rotation.components

import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket
import net.minecraft.network.packet.s2c.play.EntityS2CPacket
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventPacket
import su.mandora.tarasande.event.impl.EventRotationSet
import su.mandora.tarasande.feature.rotation.Rotations
import su.mandora.tarasande.feature.rotation.api.Rotation
import su.mandora.tarasande.mc

class CorrectRotationSet(rotations: Rotations) {

    init {
        fun abortRotation() {
            if(rotations.fakeRotation == null)
                return

            mc.player?.also { rotations.fakeRotation!!.applyOn(it) }
            rotations.fakeRotation = null
        }

        EventDispatcher.apply {
            add(EventPacket::class.java, 9999) { event ->
                if(event.type == EventPacket.Type.RECEIVE)
                    when (event.packet) {
                        is PlayerPositionLookS2CPacket ->
                            abortRotation()
                        is EntityS2CPacket ->
                            if(mc.world?.let { event.packet.getEntity(it) } == mc.player)
                                abortRotation()
                        is EntityPositionS2CPacket ->
                            if(mc.world?.getEntityById(event.packet.id) == mc.player)
                                abortRotation()
                    }
            }
            add(EventRotationSet::class.java, 1) { event ->
                rotations.fakeRotation = Rotation(event.yaw, event.pitch)
            }
        }
    }

}