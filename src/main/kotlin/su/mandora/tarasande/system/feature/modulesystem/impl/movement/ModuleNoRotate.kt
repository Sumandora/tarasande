package su.mandora.tarasande.system.feature.modulesystem.impl.movement

import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket
import su.mandora.tarasande.event.impl.EventPacket
import su.mandora.tarasande.event.impl.EventRotation
import su.mandora.tarasande.event.impl.EventRotationSet
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.util.extension.minecraft.packet.evaluateNewRotation
import su.mandora.tarasande.feature.rotation.api.Rotation

class ModuleNoRotate : Module("No rotate", "Prevents the server from rotating you", ModuleCategory.MOVEMENT) {

    private var prevRotation: Rotation? = null
    private var rotation: Rotation? = null

    init {
        registerEvent(EventPacket::class.java) { event ->
            if (event.type == EventPacket.Type.RECEIVE && event.packet is PlayerPositionLookS2CPacket) {
                if (mc.player != null) {
                    prevRotation = Rotation(mc.player!!)
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
                prevRotation!!.applyOn(mc.player!!)
                prevRotation = null
            }
        }
    }
}
