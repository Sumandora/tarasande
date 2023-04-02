package su.mandora.tarasande.system.feature.modulesystem.impl.render

import net.minecraft.entity.Entity
import su.mandora.tarasande.event.impl.EventEntityFlag
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory

class ModuleTrueSight : Module("True sight", "Makes invisible players visible", ModuleCategory.RENDER) {

    val alpha = ValueNumber(this, "Alpha", 0.0, 0.15, 1.0, 0.01)

    init {
        registerEvent(EventEntityFlag::class.java) { event ->
            if (event.flag == Entity.INVISIBLE_FLAG_INDEX)
                event.enabled = false
        }
    }
}
