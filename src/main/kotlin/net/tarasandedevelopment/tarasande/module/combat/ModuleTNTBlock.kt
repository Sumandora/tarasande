package net.tarasandedevelopment.tarasande.module.combat

import net.minecraft.entity.EntityType
import net.minecraft.entity.TntEntity
import net.tarasandedevelopment.tarasande.base.event.Event
import net.tarasandedevelopment.tarasande.base.event.Priority
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventKeyBindingIsPressed
import net.tarasandedevelopment.tarasande.mixin.accessor.ITntEntity
import net.tarasandedevelopment.tarasande.value.ValueNumber
import java.util.function.Consumer

class ModuleTNTBlock : Module("TNT block", "Auto-blocks when nearby TNT is exploding", ModuleCategory.COMBAT) {

    private val maxFuse = (TntEntity(EntityType.TNT, null) as ITntEntity).tarasande_getMaxFuse()

    private val reach = ValueNumber(this, "Reach", 0.0, 4.0, 8.0, 0.1)
    private val fuse = ValueNumber(this, "Fuse", 0.0, 30.0, maxFuse.toDouble(), 1.0)

    @Priority(1)
    val eventConsumer = Consumer<Event> { event ->
        if (event is EventKeyBindingIsPressed && event.keyBinding == mc.options.useKey) {
            val tnts = mc.world?.entities?.filterIsInstance<TntEntity>()
            if (tnts?.none { mc.player?.distanceTo(it)!! < reach.value } == true)
                return@Consumer
            if (tnts?.none { it.fuse < fuse.value } == true)
                return@Consumer

            event.pressed = true
        }
    }
}