package net.tarasandedevelopment.tarasande.module.ghost

import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventTick
import net.tarasandedevelopment.tarasande.mixin.accessor.IMinecraftClient

class ModuleFastPlace : Module("Fast place", "Speeds up block placements", ModuleCategory.GHOST) {

    init {
        registerEvent(EventTick::class.java) { event ->
            if (event.state == EventTick.State.PRE)
                (mc as IMinecraftClient).tarasande_setItemUseCooldown(0)
        }
    }

}