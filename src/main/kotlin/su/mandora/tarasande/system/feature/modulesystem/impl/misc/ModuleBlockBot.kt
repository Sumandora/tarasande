package su.mandora.tarasande.system.feature.modulesystem.impl.misc

import su.mandora.tarasande.event.impl.EventInput
import su.mandora.tarasande.event.impl.EventJump
import su.mandora.tarasande.event.impl.EventRotation
import su.mandora.tarasande.event.impl.EventVelocityYaw
import su.mandora.tarasande.feature.rotation.Rotations
import su.mandora.tarasande.feature.rotation.api.Rotation
import su.mandora.tarasande.feature.rotation.api.RotationUtil
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.util.extension.minecraft.math.plus
import su.mandora.tarasande.util.extension.minecraft.math.times
import su.mandora.tarasande.util.extension.minecraft.setMovementForward
import su.mandora.tarasande.util.extension.minecraft.setMovementSideways
import su.mandora.tarasande.util.player.PlayerUtil

class ModuleBlockBot : Module("Block bot", "Walks into the line of sight of other players", ModuleCategory.MISC) {

    private val extension = ValueNumber(this, "Extension", 0.0, 0.5, 1.0, 0.1)
    private val minDistance = ValueNumber(this, "Min distance", 0.0, 0.3, 1.0, 0.1)

    private var move = false

    init {
        registerEvent(EventRotation::class.java) { event ->
            val target = mc.world?.players?.filter { PlayerUtil.isAttackable(it) }?.minByOrNull { mc.player?.squaredDistanceTo(it)!! } ?: return@registerEvent

            val targetEye = target.eyePos + Rotation(target).forwardVector() * extension.value
            move = mc.player?.eyePos?.squaredDistanceTo(targetEye)!! > minDistance.value * minDistance.value

            val rotation = if (!move) // if he's not moving, just look at him... make him mad ^^
                RotationUtil.getRotations(mc.player?.eyePos!!, target.eyePos)
            else
                RotationUtil.getRotations(mc.player?.eyePos!!, targetEye)
            event.rotation = rotation.correctSensitivity()
        }

        registerEvent(EventJump::class.java, 1) { event ->
            if (event.state != EventJump.State.PRE) return@registerEvent
            event.yaw = Rotations.fakeRotation?.yaw ?: return@registerEvent
        }

        registerEvent(EventVelocityYaw::class.java, 1) { event ->
            event.yaw = Rotations.fakeRotation?.yaw ?: return@registerEvent
        }

        registerEvent(EventInput::class.java) { event ->
            if (event.input == mc.player?.input)
                if (move) {
                    event.input.setMovementForward(1F)
                    event.input.setMovementSideways(0F)
                    event.input.jumping = mc.player!!.horizontalCollision
                }
        }
    }

}