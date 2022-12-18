package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.movement

import net.tarasandedevelopment.tarasande.event.EventMovement
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil
import kotlin.math.cos
import kotlin.math.sin

class ModuleVehicleSpeed : Module("Vehicle speed", "Modifies vehicle movement speed", ModuleCategory.MOVEMENT) {

    private val speed = ValueNumber(this, "Speed", 0.0, 1.0, 3.0, 0.1)

    init {
        registerEvent(EventMovement::class.java) { event ->
            if (event.entity != mc.player?.vehicle)
                return@registerEvent

            if (!PlayerUtil.isPlayerMoving())
                return@registerEvent

            val rad = Math.toRadians(PlayerUtil.getMoveDirection() + 90)

            event.velocity.x = cos(rad) * speed.value
            event.velocity.z = sin(rad) * speed.value
        }
    }
}
