package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.misc

import net.tarasandedevelopment.tarasande.event.*
import net.tarasandedevelopment.tarasande.feature.rotation.Rotations
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory
import net.tarasandedevelopment.tarasande.util.extension.minecraft.plus
import net.tarasandedevelopment.tarasande.util.extension.minecraft.times
import net.tarasandedevelopment.tarasande.util.math.rotation.Rotation
import net.tarasandedevelopment.tarasande.util.math.rotation.RotationUtil
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil

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
                if (move)
                    event.movementForward = 1.0F
        }

        registerEvent(EventKeyBindingIsPressed::class.java) { event ->
            if (event.keyBinding == mc.options.jumpKey)
                event.pressed = mc.player?.horizontalCollision!!
        }
    }

}