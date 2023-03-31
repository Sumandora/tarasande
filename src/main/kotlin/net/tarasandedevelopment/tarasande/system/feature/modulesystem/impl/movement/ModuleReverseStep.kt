package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.movement

import net.minecraft.util.math.Direction
import net.tarasandedevelopment.tarasande.event.impl.EventMovement
import net.tarasandedevelopment.tarasande.event.impl.EventUpdate
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumberRange
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil

class ModuleReverseStep : Module("Reverse step", "Allows you to step down blocks", ModuleCategory.MOVEMENT) {

    private val fallDistance = ValueNumberRange(this, "Fall distance", 1.0, 1.0, 1.0, 10.0, 1.0)
    private val motion = ValueNumber(this, "Motion", 1.0, 10.0, 10.0, 1.0)

    private var wasOnGround = false

    init {
        registerEvent(EventUpdate::class.java) { event ->
            if (event.state == EventUpdate.State.POST)
                wasOnGround = mc.player?.isOnGround!!
        }
        registerEvent(EventMovement::class.java) { event ->
            if (event.entity != mc.player)
                return@registerEvent
            if (wasOnGround) {
                val fallDistance = PlayerUtil.predictFallDistance() ?: return@registerEvent
                if (fallDistance.toDouble() in this.fallDistance.let { it.minValue..it.maxValue })
                    event.velocity = event.velocity.withAxis(Direction.Axis.Y, -motion.value)
            }
        }
    }

}