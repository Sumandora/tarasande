package su.mandora.tarasande.module.ghost

import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventUpdateTargetedEntity
import su.mandora.tarasande.mixin.accessor.IGameRenderer
import su.mandora.tarasande.value.ValueNumber
import java.util.function.Consumer

class ModuleReach : Module("Reach", "Increases the hit reach", ModuleCategory.GHOST) {

    private val reach = ValueNumber(this, "Reach", 0.0, 3.0, 6.0, 0.1)

    private val origReach = (mc.gameRenderer as IGameRenderer).tarasande_getReach()

    override fun onDisable() {
        (mc.gameRenderer as IGameRenderer).tarasande_setReach(origReach)
    }

    val eventConsumer = Consumer<Event> { event ->
        if(event is EventUpdateTargetedEntity) {
            (mc.gameRenderer as IGameRenderer).tarasande_setReach(reach.value)
        }
    }

}