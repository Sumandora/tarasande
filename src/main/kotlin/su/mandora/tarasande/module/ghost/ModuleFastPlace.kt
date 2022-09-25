package su.mandora.tarasande.module.ghost

import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventTick
import su.mandora.tarasande.mixin.accessor.IMinecraftClient
import java.util.function.Consumer

class ModuleFastPlace : Module("Fast place", "Speeds up block placements", ModuleCategory.GHOST) {

    val eventConsumer = Consumer<Event> { event ->
        if (event is EventTick)
            (mc as IMinecraftClient).tarasande_setItemUseCooldown(0)
    }

}