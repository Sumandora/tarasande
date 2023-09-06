package su.mandora.tarasande.system.feature.modulesystem.impl.gamemode

import net.minecraft.util.math.Direction
import su.mandora.tarasande.event.impl.EventKeyBindingIsPressed
import su.mandora.tarasande.event.impl.EventRotation
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.util.extension.minecraft.math.minus
import su.mandora.tarasande.util.extension.minecraft.math.plus
import su.mandora.tarasande.util.extension.minecraft.math.times
import su.mandora.tarasande.util.extension.minecraft.prevPos
import su.mandora.tarasande.feature.rotation.api.Rotation
import su.mandora.tarasande.feature.rotation.api.RotationUtil
import su.mandora.tarasande.util.player.PlayerUtil

class ModuleQuakeAura : Module("Quake aura", "Aimbot for Quake-like game modes", ModuleCategory.GAMEMODE) {

    private val predict = ValueNumber(this, "Predict", 0.0, 4.0, 10.0, 1.0)
    private val aimLower = ValueNumber(this, "Aim lower", 0.0, 0.0, 2.0, 0.1)
    private val autoFire = ValueBoolean(this, "Auto fire", false)

    private var rotated = false

    init {
        registerEvent(EventRotation::class.java) { event ->
            rotated = false
            if (!mc.options.useKey.isPressed && !autoFire.value)
                return@registerEvent

            val enemy = mc.world?.entities?.filter { PlayerUtil.isAttackable(it) }?.filter { PlayerUtil.canVectorBeSeen(mc.player?.eyePos!!, it.eyePos) }?.minByOrNull { RotationUtil.getRotations(mc.player?.eyePos!!, it.eyePos).fov(Rotation(mc.player!!)) } ?: return@registerEvent

            val enemyPos = enemy.pos
            val enemyVelocity = enemyPos - enemy.prevPos()

            val extrapolatedPosition = enemy.eyePos + enemyVelocity.withAxis(Direction.Axis.Y, 0.0) * predict.value

            event.rotation = RotationUtil.getRotations(mc.player?.eyePos!!, extrapolatedPosition.subtract(0.0, aimLower.value, 0.0)).correctSensitivity()
            rotated = true
        }

        registerEvent(EventKeyBindingIsPressed::class.java) { event ->
            if (event.keyBinding == mc.options.useKey && autoFire.value && rotated)
                event.pressed = true
        }
    }

}