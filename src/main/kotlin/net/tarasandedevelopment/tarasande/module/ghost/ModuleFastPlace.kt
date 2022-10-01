package net.tarasandedevelopment.tarasande.module.ghost

import net.tarasandedevelopment.tarasande.base.event.Event
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventTick
import net.tarasandedevelopment.tarasande.mixin.accessor.IMinecraftClient
import java.util.function.Consumer

class ModuleFastPlace : Module("Fast place", "Speeds up block placements", ModuleCategory.GHOST) {

    val eventConsumer = Consumer<Event> { event ->
        if (event is EventTick)
            (mc as IMinecraftClient).tarasande_setItemUseCooldown(0)
    }

}