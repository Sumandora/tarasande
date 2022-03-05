package su.mandora.tarasande.module.movement

import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventKeyBindingIsPressed
import su.mandora.tarasande.value.ValueBoolean
import java.util.function.Consumer

class ModuleSafeWalk : Module("Safe walk", "Prevents falling off blocks", ModuleCategory.MOVEMENT) {

    private val sneak = ValueBoolean(this, "Sneak", false)

    val eventConsumer = Consumer<Event> { event ->
        when(event) {
            is EventKeyBindingIsPressed -> {
                if (event.keyBinding == mc.options.sneakKey && sneak.value)
                    if (mc.world?.isAir(mc.player?.blockPos?.add(0, -1, 0)!!)!!)
                        event.pressed = true
            }
        }
    }

}