package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.movement

import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket
import net.tarasandedevelopment.tarasande.event.EventPacket
import net.tarasandedevelopment.tarasande.event.EventRotation
import net.tarasandedevelopment.tarasande.event.EventRotationSet
import net.tarasandedevelopment.tarasande.feature.rotation.Rotations
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory
import net.tarasandedevelopment.tarasande.util.extension.minecraft.packet.evaluateNewRotation
import net.tarasandedevelopment.tarasande.util.math.rotation.Rotation

class ModuleNoRotate : Module("No rotate", "Prevents the server from rotating you", ModuleCategory.MOVEMENT) {

    private var prevRotation: Rotation? = null
    private var rotation: Rotation? = null

    init {
        registerEvent(EventPacket::class.java) { event ->
            if (event.type == EventPacket.Type.RECEIVE && event.packet is PlayerPositionLookS2CPacket) {
                if (mc.player != null) {
                    prevRotation = Rotation(mc.player!!)
                    if (Rotations.fakeRotation == null) // if this isn't the case the rotation is being handled by the RotationUtil
                        rotation = event.packet.evaluateNewRotation()
                }
            }
        }

        registerEvent(EventRotation::class.java, 1) { event ->
            if (rotation != null) {
                event.rotation = rotation!!
                rotation = null
            }
        }

        registerEvent(EventRotationSet::class.java) {
            if (prevRotation != null) {
                mc.player?.yaw = prevRotation!!.yaw
                mc.player?.pitch = prevRotation!!.pitch
                prevRotation = null
            }
        }
    }
}
