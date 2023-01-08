package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.movement

import net.minecraft.entity.MovementType
import net.tarasandedevelopment.tarasande.event.EventKeyBindingIsPressed
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueMode
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory
import net.tarasandedevelopment.tarasande.util.extension.mc
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil
import net.tarasandedevelopment.tarasande.util.player.prediction.PredictionEngine
import net.tarasandedevelopment.tarasande.util.player.prediction.with

class ModuleSafeWalk : Module("Safe walk", "Prevents falling off blocks", ModuleCategory.MOVEMENT) {

    val sneak = ValueBoolean(this, "Sneak", false)
    private val offGround = object : ValueMode(this, "Off-ground", false, "Force disable", "Force enable", "Ignore") {
        override fun isEnabled() = sneak.value
    }
    private val extrapolation = object : ValueNumber(this, "Extrapolation", 0.0, 1.0, 10.0, 1.0) {
        override fun isEnabled() = sneak.value
    }

    init {
        registerEvent(EventKeyBindingIsPressed::class.java) { event ->
            if (event.keyBinding == mc.options.sneakKey && sneak.value && PlayerUtil.isPlayerMoving()) {
                val onEdge = PredictionEngine.predictState(extrapolation.value.toInt(), input = mc.player?.input?.with(sneaking = true)).first.let { prediction ->
                    mc.player?.velocity?.let { prediction.adjustMovementForSneaking(it, MovementType.SELF) != it }!!
                }
                event.pressed = event.pressed || when {
                    offGround.isSelected(0) -> mc.player?.isOnGround == true && onEdge
                    offGround.isSelected(1) -> mc.player?.isOnGround == false || onEdge
                    offGround.isSelected(2) -> onEdge
                    else -> true
                }
            }
        }
    }
}
