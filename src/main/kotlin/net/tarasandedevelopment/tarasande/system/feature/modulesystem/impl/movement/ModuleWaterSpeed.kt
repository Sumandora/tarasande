package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.movement

import net.tarasandedevelopment.tarasande.event.EventMovement
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory
import net.tarasandedevelopment.tarasande.util.extension.mc
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil

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