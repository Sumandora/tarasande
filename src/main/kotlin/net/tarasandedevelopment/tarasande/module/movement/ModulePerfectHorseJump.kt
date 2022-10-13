package net.tarasandedevelopment.tarasande.module.movement

import net.minecraft.client.MinecraftClient
import net.tarasandedevelopment.tarasande.base.event.Event
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventUpdate
import net.tarasandedevelopment.tarasande.mixin.accessor.IClientPlayerEntity
import net.tarasandedevelopment.tarasande.value.ValueNumber
import java.util.function.Consumer

class ModulePerfectHorseJump : Module("Perfect horse jump", "Forces perfect horse jumps", ModuleCategory.MOVEMENT) {

    private val jumpPower = ValueNumber(this, "Jump power", 0.1, 1.0, 1.0, 1E-2)
    private val jumpPowerCounter = ValueNumber(this, "Jump power: counter", 1.0, 9.0, 9.0, 1E-2)

    val eventConsumer = Consumer<Event> {
        if (it is EventUpdate && it.state == EventUpdate.State.PRE) {
            (MinecraftClient.getInstance().player as IClientPlayerEntity).tarasande_setMountJumpStrength(this.jumpPower.value.toFloat())
            (MinecraftClient.getInstance().player as IClientPlayerEntity).tarasande_setField_3938(this.jumpPowerCounter.value.toInt())
        }
    }
}
