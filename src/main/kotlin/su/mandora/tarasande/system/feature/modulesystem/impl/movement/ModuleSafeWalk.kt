package su.mandora.tarasande.system.feature.modulesystem.impl.movement

import net.minecraft.entity.MovementType
import net.minecraft.entity.player.PlayerEntity
import su.mandora.tarasande.event.impl.EventInput
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumberRange
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.util.player.prediction.PredictionEngine
import su.mandora.tarasande.util.player.prediction.with

class ModuleSafeWalk : Module("Safe walk", "Prevents falling off blocks", ModuleCategory.MOVEMENT) {

    val clipInAir = ValueBoolean(this, "Clip in air", false)
    val sneak = ValueBoolean(this, "Sneak", false)
    private val offGround = ValueMode(this, "Off-ground", false, "Force disable", "Force enable", "Ignore", isEnabled = { sneak.value })
    private val extrapolation = ValueNumberRange(this, "Extrapolation", 0.0, 1.0, 3.0, 10.0, 1.0, isEnabled = { sneak.value })
    private val unsneakDelay = ValueNumberRange(this, "Unsneak delay", 0.0, 1.0, 3.0, 10.0, 1.0, isEnabled = { sneak.value })

    private var lastSneak = Int.MIN_VALUE

    private var nextUnsneakDelay = 0
    private var nextExtrapolation = 0

    init {
        nextExtrapolation = extrapolation.randomNumber().toInt()
        nextUnsneakDelay = unsneakDelay.randomNumber().toInt()
    }

    init {
        registerEvent(EventInput::class.java) { event ->
            if(event.input != mc.player?.input)
                return@registerEvent

            if (sneak.value && mc.player!!.velocity.horizontalLengthSquared() > 0.0) {
                val onEdge = PredictionEngine.predictState(nextExtrapolation, input = mc.player!!.input.with(sneaking = true)).first.let { prediction ->
                    mc.player!!.velocity.let { (prediction as PlayerEntity).adjustMovementForSneaking(it, MovementType.SELF) != it }
                }
                val forceSneak = when {
                    offGround.isSelected(0) -> mc.player!!.isOnGround && onEdge
                    offGround.isSelected(1) -> !mc.player!!.isOnGround || onEdge
                    offGround.isSelected(2) -> onEdge
                    else -> true
                }

                if (forceSneak) {
                    lastSneak = mc.player!!.age
                    nextExtrapolation = extrapolation.randomNumber().toInt()
                    nextUnsneakDelay = unsneakDelay.randomNumber().toInt()
                }

                event.input.sneaking = event.input.sneaking || forceSneak || (mc.player!!.age - lastSneak < nextUnsneakDelay)
            }
        }
    }
}
