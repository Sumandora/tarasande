package net.tarasandedevelopment.tarasande.systems.feature.modulesystem.impl.movement

import net.minecraft.client.MinecraftClient
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.events.impl.EventUpdate
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.ModuleCategory

class ModulePerfectHorseJump : Module("Perfect horse jump", "Forces perfect horse jumps", ModuleCategory.MOVEMENT) {

    private val jumpPower = ValueNumber(this, "Jump power", 0.1, 1.0, 1.0, 1E-2)
    private val jumpPowerCounter = ValueNumber(this, "Jump power: counter", 1.0, 9.0, 9.0, 1E-2)

    init {
        registerEvent(EventUpdate::class.java) { event ->
            if (event.state == EventUpdate.State.PRE) {
                MinecraftClient.getInstance().player?.also {
                    it.mountJumpStrength = this.jumpPower.value.toFloat()
                    it.field_3938 = this.jumpPowerCounter.value.toInt()
                }
            }
        }
    }
}
