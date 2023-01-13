package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.movement

import net.tarasandedevelopment.tarasande.event.EventKeyBindingIsPressed
import net.tarasandedevelopment.tarasande.event.EventUpdate
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueMode
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil

class ModuleParkour : Module("Parkour", "Jumps when falling off ledges", ModuleCategory.MOVEMENT) {

    private val detectionMethod = ValueMode(this, "Detection method", false, "Extrapolation", "Ground")
    private val extrapolation = object : ValueNumber(this, "Extrapolation", 0.0, 1.0, 10.0, 1.0) {
        override fun isEnabled() = detectionMethod.isSelected(0)
    }

    private var wasOnGround = false

    init {
        registerEvent(EventUpdate::class.java) { event ->
            if (event.state == EventUpdate.State.PRE) {
                if (detectionMethod.isSelected(1) && wasOnGround && mc.player?.isOnGround == false && mc.player?.velocity?.y!! < 0.0) {
                    mc.player?.jump()
                }
                wasOnGround = mc.player?.isOnGround == true
            }
        }

        registerEvent(EventKeyBindingIsPressed::class.java) { event ->
            if (event.keyBinding == mc.options.jumpKey) {
                if (detectionMethod.isSelected(0) && PlayerUtil.isOnEdge(extrapolation.value))
                    event.pressed = true
            }
        }
    }
}
