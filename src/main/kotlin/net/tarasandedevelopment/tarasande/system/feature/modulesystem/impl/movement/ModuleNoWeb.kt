package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.movement

import net.minecraft.block.CobwebBlock
import net.minecraft.util.math.Vec3d
import net.tarasandedevelopment.tarasande.event.impl.EventBlockCollision
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory

class ModuleNoWeb : Module("No web", "Prevents cobwebs' slowdown", ModuleCategory.MOVEMENT) {

    @Suppress("MemberVisibilityCanBePrivate") // protocol hack package
    val horizontalSlowdown = ValueNumber(this, "Horizontal slowdown", 0.0, 0.25, 1.0, 0.01)

    @Suppress("MemberVisibilityCanBePrivate") // protocol hack package
    val verticalSlowdown = ValueNumber(this, "Vertical slowdown", 0.0, 0.05, 1.0, 0.01)

    init {
        registerEvent(EventBlockCollision::class.java) { event ->
            if (event.entity != mc.player)
                return@registerEvent
            if (event.state.block !is CobwebBlock)
                return@registerEvent

            event.cancelled = true
            mc.player?.slowMovement(event.state, Vec3d(horizontalSlowdown.value, verticalSlowdown.value, horizontalSlowdown.value))
        }
    }
}
