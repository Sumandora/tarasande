package su.mandora.tarasande.system.feature.modulesystem.impl.movement

import su.mandora.tarasande.event.impl.EventKeyBindingIsPressed
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.util.player.prediction.PredictionEngine
import su.mandora.tarasande.util.player.prediction.with

class ModuleSafeWalk : Module("Safe walk", "Prevents falling off blocks", ModuleCategory.MOVEMENT) {

    val clipInAir = ValueBoolean(this, "Clip in air", false)
    val sneak = ValueBoolean(this, "Sneak", false)
    private val offGround = ValueMode(this, "Off-ground", false, "Force disable", "Force enable", "Ignore", isEnabled = { sneak.value })
    private val extrapolation = ValueNumber(this, "Extrapolation", 0.0, 1.0, 10.0, 1.0, isEnabled = { sneak.value })
    private val unsneakDelay = ValueNumber(this, "Unsneak delay", 0.0, 0.0, 10.0, 1.0, isEnabled = { sneak.value })

    private var lastSneak = Int.MIN_VALUE

    init {
        registerEvent(EventKeyBindingIsPressed::class.java) { event -> // TODO Fix
            if(event.keyBinding != mc.options.sneakKey)
                return@registerEvent

            if(mc.player == null)
                return@registerEvent

            if (sneak.value && mc.player!!.velocity.horizontalLengthSquared() > 0.0) {
                val onEdge = PredictionEngine.predictState(extrapolation.value.toInt(), input = mc.player!!.input.with(sneaking = false)).first.let { prediction ->
                    prediction.y < mc.player!!.y
                }
                val forceSneak = when {
                    offGround.isSelected(0) -> mc.player!!.isOnGround && onEdge
                    offGround.isSelected(1) -> !mc.player!!.isOnGround || onEdge
                    offGround.isSelected(2) -> onEdge
                    else -> true
                }

                if (forceSneak)
                    lastSneak = mc.player!!.age

                event.pressed = event.pressed || forceSneak || (mc.player!!.age - lastSneak < unsneakDelay.value)
            }
        }
    }
}
