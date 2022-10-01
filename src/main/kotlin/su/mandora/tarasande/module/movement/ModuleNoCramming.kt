package su.mandora.tarasande.module.movement

import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventCanBePushedBy
import su.mandora.tarasande.value.ValueMode
import java.util.function.Consumer
import java.util.function.Predicate

class ModuleNoCramming : Module("No cramming", "Prevents entity cramming", ModuleCategory.MOVEMENT) {

    val mode = ValueMode(this, "Mode", false, "Force enable", "Force disable")

    val eventConsumer = Consumer<Event> { event ->
        if (event is EventCanBePushedBy) {
            if (event.entity == mc.player)
                event.predicate = Predicate { mode.isSelected(0) }
        }
    }

}