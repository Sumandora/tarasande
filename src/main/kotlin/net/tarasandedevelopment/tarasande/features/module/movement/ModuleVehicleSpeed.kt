package net.tarasandedevelopment.tarasande.features.module.movement

import net.tarasandedevelopment.tarasande.base.features.module.Module
import net.tarasandedevelopment.tarasande.base.features.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventMovement
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil
import net.tarasandedevelopment.tarasande.value.ValueNumber
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
