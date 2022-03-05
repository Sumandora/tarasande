package su.mandora.tarasande.module.movement

import net.minecraft.client.MinecraftClient
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventHasForwardMovement
import su.mandora.tarasande.event.EventIsWalking
import su.mandora.tarasande.event.EventKeyBindingIsPressed
import su.mandora.tarasande.value.ValueBoolean
import java.util.function.Consumer

class ModuleSprint : Module("Sprint", "Automatically sprints", ModuleCategory.MOVEMENT) {

    private val allowBackwards = object : ValueBoolean(this, "Allow backwards", false) {
        override fun isVisible() = !TarasandeMain.get().clientValues?.correctMovement?.isSelected(1)!!
    }

    val eventConsumer = Consumer<Event> { event ->
        when (event) {
            is EventKeyBindingIsPressed -> {
                if (event.keyBinding == mc.options?.sprintKey)
                    event.pressed = true
            }
            is EventIsWalking -> {
                if (allowBackwards.isVisible() && allowBackwards.value)
                    event.walking = MinecraftClient.getInstance().player?.input?.movementInput?.lengthSquared()!! > 0.8f * 0.8f
            }
            is EventHasForwardMovement -> {
                if (allowBackwards.isVisible() && allowBackwards.value)
                    event.hasForwardMovement = MinecraftClient.getInstance().player?.input?.movementInput?.lengthSquared()!! > 0.8f * 0.8f
            }
        }
    }

}