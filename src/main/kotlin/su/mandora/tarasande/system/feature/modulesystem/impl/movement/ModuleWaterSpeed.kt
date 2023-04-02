package su.mandora.tarasande.system.feature.modulesystem.impl.movement

import su.mandora.tarasande.event.impl.EventMovement
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.util.player.PlayerUtil

class ModuleWaterSpeed : Module("Water speed", "Modifies movement speed inside fluids", ModuleCategory.MOVEMENT) {

    private val horizontalMultiplier = ValueNumber(this, "Horizontal multiplier", 0.0, 1.0, 3.0, 0.1)
    private val verticalMultiplier = ValueNumber(this, "Vertical multiplier", 0.0, 1.0, 3.0, 0.1)
    private val disablePushing = ValueBoolean(this, "Disable pushing", false)

    init {
        registerEvent(EventMovement::class.java) { event ->
            if (mc.player?.isTouchingWater == true || mc.player?.isInLava == true) {
                event.velocity = event.velocity.multiply(
                    horizontalMultiplier.value,
                    verticalMultiplier.value,
                    horizontalMultiplier.value
                )
                if (disablePushing.value && !PlayerUtil.isPlayerMoving()) {
                    event.velocity = event.velocity.multiply(0.0, 1.0, 0.0)
                }
            }
        }
    }

}