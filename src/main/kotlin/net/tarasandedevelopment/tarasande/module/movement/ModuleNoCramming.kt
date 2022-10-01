package net.tarasandedevelopment.tarasande.module.movement

import net.tarasandedevelopment.tarasande.base.event.Event
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventCanBePushedBy
import net.tarasandedevelopment.tarasande.value.ValueMode
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