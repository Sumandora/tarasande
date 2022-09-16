package su.mandora.tarasande.module.combat

import net.minecraft.entity.EntityType
import net.minecraft.entity.TntEntity
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.event.Priority
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventKeyBindingIsPressed
import su.mandora.tarasande.mixin.accessor.ITntEntity
import su.mandora.tarasande.value.ValueNumber
import java.util.function.Consumer

class ModuleTNTBlock : Module("TNT block", "Auto-blocks when nearby TNT is exploding", ModuleCategory.COMBAT) {

    private val maxFuse = (TntEntity(EntityType.TNT, null) as ITntEntity).tarasande_getMaxFuse()

    private val reach = ValueNumber(this, "Reach", 0.0, 4.0, 8.0, 0.1)
    private val fuse = ValueNumber(this, "Fuse", 0.0, 30.0, maxFuse.toDouble(), 1.0)

    @Priority(1)
    val eventConsumer = Consumer<Event> { event ->
        if (event is EventKeyBindingIsPressed && event.keyBinding == mc.options.useKey) {
            val tnts = mc.world?.entities?.filter { it is TntEntity }
            if (tnts?.none { mc.player?.distanceTo(it)!! < reach.value } == true)
                return@Consumer
            if (tnts?.none { (it as TntEntity).fuse < fuse.value } == true)
                return@Consumer

            event.pressed = true
        }
    }
}