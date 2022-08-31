package su.mandora.tarasande.module.movement

import net.minecraft.client.MinecraftClient
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket
import net.minecraft.util.math.MathHelper
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.event.Priority
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventPacket
import su.mandora.tarasande.event.EventPollEvents
import su.mandora.tarasande.event.EventRotationSet
import su.mandora.tarasande.util.math.rotation.Rotation
import su.mandora.tarasande.util.math.rotation.RotationUtil
import su.mandora.tarasande.value.ValueNumberRange
import java.util.function.Consumer

class ModuleNoRotate : Module("No rotate", "Prevents the server from rotating you", ModuleCategory.MOVEMENT) {

    private val recoverSpeed = ValueNumberRange(this, "Recover speed", 0.1, 1.0, 1.0, 1.0, 0.1)

    var prevRotation: Rotation? = null
    var rotation: Rotation? = null

    private fun wrapRotation(rotation: Rotation) {
        rotation.yaw = rotation.yaw % 360.0f
        rotation.pitch = MathHelper.clamp(rotation.pitch, -90.0f, 90.0f) % 360.0f
    }

    fun evaluateNewRotation(packet: PlayerPositionLookS2CPacket): Rotation {
        var j = packet.yaw
        var k = packet.pitch
        if (packet.flags.contains(PlayerPositionLookS2CPacket.Flag.X_ROT)) {
            k += MinecraftClient.getInstance().player?.pitch!!
        }
        if (packet.flags.contains(PlayerPositionLookS2CPacket.Flag.Y_ROT)) {
            j += MinecraftClient.getInstance().player?.yaw!!
        }
        // The pitch calculation is literally mojang dev iq overload, kept for historic reasons
        val rot = Rotation(j, k)
        wrapRotation(rot)
        return rot
    }

    @Priority(1) // the rotation should always be overridden
    val eventConsumer = Consumer<Event> { event ->
        when (event) {
            is EventPacket -> {
                if (event.type == EventPacket.Type.RECEIVE && event.packet is PlayerPositionLookS2CPacket) {
                    if(mc.player != null) {
                        prevRotation = Rotation(mc.player!!)
                        if (RotationUtil.fakeRotation == null) // if this isnt the case the rotation is being handled by the RotationUtil
                            rotation = evaluateNewRotation(event.packet)
                    }
                }
            }

            is EventPollEvents -> {
                if (rotation != null) {
                    event.rotation = rotation!!
                    event.minRotateToOriginSpeed = recoverSpeed.minValue
                    event.maxRotateToOriginSpeed = recoverSpeed.maxValue

                    rotation = null
                }
            }

            is EventRotationSet -> {
                if(prevRotation != null) {
                    mc.player?.yaw = prevRotation?.yaw!!
                    mc.player?.pitch = prevRotation?.pitch!!
                }
            }
        }
    }

}