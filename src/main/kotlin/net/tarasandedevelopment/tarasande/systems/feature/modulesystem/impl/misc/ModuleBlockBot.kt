package net.tarasandedevelopment.tarasande.systems.feature.modulesystem.impl.misc

import net.minecraft.client.MinecraftClient
import net.tarasandedevelopment.tarasande.event.EventGoalMovement
import net.tarasandedevelopment.tarasande.event.EventInput
import net.tarasandedevelopment.tarasande.event.EventKeyBindingIsPressed
import net.tarasandedevelopment.tarasande.event.EventPollEvents
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.ModuleCategory
import net.tarasandedevelopment.tarasande.util.extension.plus
import net.tarasandedevelopment.tarasande.util.math.rotation.Rotation
import net.tarasandedevelopment.tarasande.util.math.rotation.RotationUtil
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil

class ModuleBlockBot : Module("Block bot", "Walks into the line of sight of other players", ModuleCategory.MISC) {

    private val extension = ValueNumber(this, "Extension", 0.0, 0.5, 1.0, 0.1)
    private val minDistance = ValueNumber(this, "Min distance", 0.0, 0.3, 1.0, 0.1)

    private var move = false

    init {
        registerEvent(EventPollEvents::class.java) { event ->
            val target = mc.world?.players?.filter { PlayerUtil.isAttackable(it) }?.minByOrNull { mc.player?.squaredDistanceTo(it)!! } ?: return@registerEvent

            val targetEye = target.eyePos + Rotation(target).forwardVector(extension.value)
            move = mc.player?.eyePos?.squaredDistanceTo(targetEye)!! > minDistance.value * minDistance.value

            val rotation = if (!move) // if he's not moving, just look at him... make him mad ^^
                RotationUtil.getRotations(mc.player?.eyePos!!, target.eyePos)
            else
                RotationUtil.getRotations(mc.player?.eyePos!!, targetEye)
            event.rotation = rotation.correctSensitivity()
        }

        registerEvent(EventGoalMovement::class.java) { event ->
            event.yaw = RotationUtil.fakeRotation?.yaw ?: return@registerEvent
        }

        registerEvent(EventInput::class.java) { event ->
            if (event.input == MinecraftClient.getInstance().player?.input)
                if (move)
                    event.movementForward = 1.0f
        }

        registerEvent(EventKeyBindingIsPressed::class.java) { event ->
            if (event.keyBinding == mc.options.jumpKey)
                event.pressed = mc.player?.horizontalCollision!!
        }
    }

}