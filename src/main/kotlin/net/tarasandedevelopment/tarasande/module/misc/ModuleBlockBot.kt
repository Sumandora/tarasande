package net.tarasandedevelopment.tarasande.module.misc

import net.minecraft.client.MinecraftClient
import net.tarasandedevelopment.tarasande.base.event.Event
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventGoalMovement
import net.tarasandedevelopment.tarasande.event.EventInput
import net.tarasandedevelopment.tarasande.event.EventKeyBindingIsPressed
import net.tarasandedevelopment.tarasande.event.EventPollEvents
import net.tarasandedevelopment.tarasande.util.extension.plus
import net.tarasandedevelopment.tarasande.util.math.rotation.Rotation
import net.tarasandedevelopment.tarasande.util.math.rotation.RotationUtil
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil
import net.tarasandedevelopment.tarasande.value.ValueNumber
import java.util.function.Consumer

class ModuleBlockBot : Module("Block bot", "Walks into the line of sight of other players", ModuleCategory.MISC) {

    private val extension = ValueNumber(this, "Extension", 0.0, 0.5, 1.0, 0.1)
    private val minDistance = ValueNumber(this, "Min distance", 0.0, 0.3, 1.0, 0.1)

    private var move = false

    val eventConsumer = Consumer<Event> { event ->
        when (event) {
            is EventPollEvents -> {
                val target = mc.world?.players?.filter { PlayerUtil.isAttackable(it) }?.minByOrNull { mc.player?.squaredDistanceTo(it)!! } ?: return@Consumer

                val targetEye = target.eyePos + Rotation(target).forwardVector(extension.value)
                move = mc.player?.eyePos?.squaredDistanceTo(targetEye)!! > minDistance.value * minDistance.value

                val rotation = if (!move) // if he's not moving, just look at him... make him mad ^^
                    RotationUtil.getRotations(mc.player?.eyePos!!, target.eyePos)
                else
                    RotationUtil.getRotations(mc.player?.eyePos!!, targetEye)
                event.rotation = rotation.correctSensitivity()
            }

            is EventGoalMovement -> {
                event.yaw = RotationUtil.fakeRotation?.yaw ?: return@Consumer
            }

            is EventInput -> {
                if (event.input == MinecraftClient.getInstance().player?.input)
                    if (move)
                        event.movementForward = 1.0f
            }

            is EventKeyBindingIsPressed -> {
                if (event.keyBinding == mc.options.jumpKey)
                    event.pressed = mc.player?.horizontalCollision!!
            }
        }
    }

}