package net.tarasandedevelopment.tarasande.module.render

import net.minecraft.entity.Entity
import net.tarasandedevelopment.eventsystem.Event
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventEntityFlag
import net.tarasandedevelopment.tarasande.mixin.accessor.IEntity
import net.tarasandedevelopment.tarasande.value.ValueNumber
import java.util.function.Consumer

class ModuleTrueSight : Module("True sight", "Makes invisible players visible", ModuleCategory.RENDER) {

    val alpha = ValueNumber(this, "Alpha", 0.0, 0.15, 1.0, 0.01)

    init {
        registerEvent(EventEntityFlag::class.java) { event ->
            if (event.flag == Entity.INVISIBLE_FLAG_INDEX)
                event.enabled = false
        }
    }
}
