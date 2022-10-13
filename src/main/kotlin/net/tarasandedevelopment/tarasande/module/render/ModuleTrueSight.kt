package net.tarasandedevelopment.tarasande.module.render

import net.tarasandedevelopment.tarasande.base.event.Event
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventEntityFlag
import net.tarasandedevelopment.tarasande.mixin.accessor.IEntity
import net.tarasandedevelopment.tarasande.value.ValueNumber
import java.util.function.Consumer

class ModuleTrueSight : Module("True sight", "Makes invisible players visible", ModuleCategory.RENDER) {

    val alpha = ValueNumber(this, "Alpha", 0.0, 0.15, 1.0, 0.01)

    val eventConsumer = Consumer<Event> { event ->
        if (event is EventEntityFlag) {
            if (event.flag == (event.entity as IEntity).tarasande_getInvisibleFlagIndex())
                event.enabled = false
        }
    }

}