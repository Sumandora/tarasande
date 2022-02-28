package su.mandora.tarasande.module.render

import net.minecraft.entity.Entity
import net.minecraft.entity.decoration.ArmorStandEntity
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory

class ModuleESP : Module("ESP", "Makes entities visible behind walls", ModuleCategory.RENDER) {

    fun filter(entity: Entity) = entity !is ArmorStandEntity

}