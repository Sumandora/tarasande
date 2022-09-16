package su.mandora.tarasande.module.movement

import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventInput
import su.mandora.tarasande.event.EventKeyBindingIsPressed
import su.mandora.tarasande.value.ValueBoolean
import java.util.function.Consumer

class ModuleSneak : Module("Sneak", "Automatically sneaks", ModuleCategory.MOVEMENT) {

    private val dontSlowdown = ValueBoolean(this, "Don't slowdown", true)

    val eventConsumer = Consumer<Event> { event ->
        when (event) {
            is EventKeyBindingIsPressed ->
                if (event.keyBinding == mc.options.sneakKey)
                    event.pressed = true

            is EventInput ->
                if (dontSlowdown.value)
                    event.slowDown = false
        }
    }

}