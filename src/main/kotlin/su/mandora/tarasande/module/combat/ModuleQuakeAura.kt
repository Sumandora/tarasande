package su.mandora.tarasande.module.combat

import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventKeyBindingIsPressed
import su.mandora.tarasande.event.EventPollEvents
import su.mandora.tarasande.util.extension.minus
import su.mandora.tarasande.util.extension.plus
import su.mandora.tarasande.util.extension.times
import su.mandora.tarasande.util.math.rotation.Rotation
import su.mandora.tarasande.util.math.rotation.RotationUtil
import su.mandora.tarasande.util.player.PlayerUtil
import su.mandora.tarasande.value.ValueBoolean
import su.mandora.tarasande.value.ValueNumber
import java.util.function.Consumer

class ModuleQuakeAura : Module("Quake aura", "Aimbot for Quake-like game modes", ModuleCategory.COMBAT) {

    private val predict = ValueNumber(this, "Predict", 0.0, 4.0, 10.0, 1.0)
    private val aimLower = ValueNumber(this, "Aim lower", 0.0, 0.0, 2.0, 0.1)
    private val autoFire = ValueBoolean(this, "Auto fire", false)
    private val lockView = ValueBoolean(this, "Lock view", true)

    private var rotated = false

    val eventConsumer = Consumer<Event> { event ->
        when (event) {
            is EventPollEvents -> {
                rotated = false
                if (!mc.options.useKey.isPressed && !autoFire.value)
                    return@Consumer

                val enemy = mc.world?.entities?.filter { PlayerUtil.isAttackable(it) }?.filter { PlayerUtil.canVectorBeSeen(mc.player?.eyePos!!, it.eyePos) }?.minByOrNull { RotationUtil.getRotations(mc.player?.eyePos!!, it.eyePos).fov(Rotation(mc.player!!)) } ?: return@Consumer

                val enemyPos = enemy.pos
                val enemyVelocity = enemyPos - Vec3d(enemy.lastRenderX, enemy.lastRenderY, enemy.lastRenderZ)

                val extrapolatedPosition = enemy.eyePos + enemyVelocity.withAxis(Direction.Axis.Y, 0.0) * predict.value

                event.rotation = RotationUtil.getRotations(mc.player?.eyePos!!, extrapolatedPosition.subtract(0.0, aimLower.value, 0.0)).correctSensitivity()
                if (lockView.value) {
                    mc.player?.yaw = event.rotation.yaw
                    mc.player?.pitch = event.rotation.pitch
                }
                rotated = true
            }

            is EventKeyBindingIsPressed -> {
                if (event.keyBinding == mc.options.useKey && autoFire.value && rotated)
                    event.pressed = true
            }
        }
    }

}