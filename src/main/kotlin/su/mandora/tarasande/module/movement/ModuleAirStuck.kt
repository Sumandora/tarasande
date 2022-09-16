package su.mandora.tarasande.module.movement

import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventUpdate
import java.util.function.Consumer

class ModuleAirStuck : Module("Air stuck", "Freezes all movement", ModuleCategory.MOVEMENT) {

    val eventConsumer = Consumer<Event> { event ->
        if (event is EventUpdate && event.state == EventUpdate.State.PRE)
            event.cancelled = true
    }

}