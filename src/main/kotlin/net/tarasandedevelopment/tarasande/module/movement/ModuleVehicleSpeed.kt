package net.tarasandedevelopment.tarasande.module.movement

import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventMovement
import net.tarasandedevelopment.tarasande.mixin.accessor.IVec3d
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
            val accessor = event.velocity as IVec3d

            accessor.tarasande_setX(cos(rad) * speed.value)
            accessor.tarasande_setZ(sin(rad) * speed.value)
        }
    }

}