package su.mandora.tarasande.module.misc

import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventGoalMovement
import su.mandora.tarasande.event.EventInput
import su.mandora.tarasande.event.EventKeyBindingIsPressed
import su.mandora.tarasande.event.EventPollEvents
import su.mandora.tarasande.util.math.rotation.Rotation
import su.mandora.tarasande.util.math.rotation.RotationUtil
import su.mandora.tarasande.util.player.PlayerUtil
import su.mandora.tarasande.value.ValueNumber
import java.util.function.Consumer

class ModuleBlockBot : Module("Block bot", "Walks into the line of sight of other players", ModuleCategory.MISC) {

    private val extension = ValueNumber(this, "Extension", 0.0, 0.5, 1.0, 0.1)
    private val minDistance = ValueNumber(this, "Min distance", 0.0, 0.3, 1.0, 0.1)

    private var move = false

    val eventConsumer = Consumer<Event> { event ->
        when (event) {
            is EventPollEvents -> {
                val target = mc.world?.players?.filter { PlayerUtil.isAttackable(it) }?.minByOrNull { mc.player?.squaredDistanceTo(it)!! } ?: return@Consumer

                val targetEye = target.eyePos.add(Rotation(target).forwardVector(extension.value))
                move = mc.player?.eyePos?.squaredDistanceTo(targetEye)!! > minDistance.value * minDistance.value

                val rotation = if (!move) // if hes not moving, just look at him... make him mad ^^
                    RotationUtil.getRotations(mc.player?.eyePos!!, target.eyePos)
                else
                    RotationUtil.getRotations(mc.player?.eyePos!!, targetEye)
                event.rotation = rotation.correctSensitivity()
            }

            is EventGoalMovement -> {
                event.yaw = RotationUtil.fakeRotation?.yaw ?: return@Consumer
            }

            is EventInput -> {
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