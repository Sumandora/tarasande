package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.combat

import net.minecraft.entity.TntEntity
import net.tarasandedevelopment.tarasande.event.EventKeyBindingIsPressed
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory

class ModuleTNTBlock : Module("TNT block", "Auto-blocks when nearby TNT is exploding", ModuleCategory.COMBAT) {

    private val reach = ValueNumber(this, "Reach", 0.1, 4.0, 8.0, 0.1)
    private val fuse = ValueNumber(this, "Fuse", 0.0, 30.0, TntEntity.DEFAULT_FUSE.toDouble(), 1.0)

    init {
        registerEvent(EventKeyBindingIsPressed::class.java, 1) { event ->
            if (event.keyBinding == mc.options.useKey) {
                val tnts = mc.world?.entities?.filterIsInstance<TntEntity>()
                if (tnts?.none { mc.player?.distanceTo(it)!! < reach.value } == true)
                    return@registerEvent
                if (tnts?.none { it.fuse < fuse.value } == true)
                    return@registerEvent

                event.pressed = true
            }
        }
    }
}