package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.movement

import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket
import net.tarasandedevelopment.tarasande.event.EventPacket
import net.tarasandedevelopment.tarasande.event.EventPollEvents
import net.tarasandedevelopment.tarasande.event.EventRotationSet
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumberRange
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory
import net.tarasandedevelopment.tarasande.util.math.rotation.Rotation
import net.tarasandedevelopment.tarasande.util.math.rotation.RotationUtil

class ModuleNoRotate : Module("No rotate", "Prevents the server from rotating you", ModuleCategory.MOVEMENT) {

    private val recoverSpeed = ValueNumberRange(this, "Recover speed", 0.1, 1.0, 1.0, 1.0, 0.1)

    private var prevRotation: Rotation? = null
    private var rotation: Rotation? = null

    init {
        registerEvent(EventPacket::class.java) { event ->
            if (event.type == EventPacket.Type.RECEIVE && event.packet is PlayerPositionLookS2CPacket) {
                if(mc.player != null) {
                    prevRotation = Rotation(mc.player!!)
                    if (RotationUtil.fakeRotation == null) // if this isn't the case the rotation is being handled by the RotationUtil
                        rotation = RotationUtil.evaluateNewRotation(event.packet)
                }
            }
        }

        registerEvent(EventPollEvents::class.java, 1) { event ->
            if (rotation != null) {
                event.rotation = rotation!!
                event.minRotateToOriginSpeed = recoverSpeed.minValue
                event.maxRotateToOriginSpeed = recoverSpeed.maxValue

                rotation = null
            }
        }

        registerEvent(EventRotationSet::class.java) {
            if(prevRotation != null) {
                mc.player?.yaw = prevRotation?.yaw!!
                mc.player?.pitch = prevRotation?.pitch!!
            }
        }
    }
}
