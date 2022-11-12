package net.tarasandedevelopment.tarasande.systems.feature.modulesystem.impl.combat

import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.events.impl.EventKeyBindingIsPressed
import net.tarasandedevelopment.events.impl.EventPollEvents
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.ModuleCategory
import net.tarasandedevelopment.tarasande.util.extension.minus
import net.tarasandedevelopment.tarasande.util.extension.plus
import net.tarasandedevelopment.tarasande.util.extension.times
import net.tarasandedevelopment.tarasande.util.math.rotation.Rotation
import net.tarasandedevelopment.tarasande.util.math.rotation.RotationUtil
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil

class ModuleQuakeAura : Module("Quake aura", "Aimbot for Quake-like game modes", ModuleCategory.COMBAT) {

    private val predict = ValueNumber(this, "Predict", 0.0, 4.0, 10.0, 1.0)
    private val aimLower = ValueNumber(this, "Aim lower", 0.0, 0.0, 2.0, 0.1)
    private val autoFire = ValueBoolean(this, "Auto fire", false)
    private val lockView = ValueBoolean(this, "Lock view", true)

    private var rotated = false

    init {
        registerEvent(EventPollEvents::class.java) { event ->
            rotated = false
            if (!mc.options.useKey.isPressed && !autoFire.value)
                return@registerEvent

            val enemy = mc.world?.entities?.filter { PlayerUtil.isAttackable(it) }?.filter { PlayerUtil.canVectorBeSeen(mc.player?.eyePos!!, it.eyePos) }?.minByOrNull { RotationUtil.getRotations(mc.player?.eyePos!!, it.eyePos).fov(Rotation(mc.player!!)) } ?: return@registerEvent

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

        registerEvent(EventKeyBindingIsPressed::class.java) { event ->
            if (event.keyBinding == mc.options.useKey && autoFire.value && rotated)
                event.pressed = true
        }
    }

}