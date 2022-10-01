package su.mandora.tarasande.module.ghost

import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventUpdateTargetedEntity
import net.tarasandedevelopment.tarasande.mixin.accessor.IGameRenderer
import su.mandora.tarasande.value.ValueNumber
import java.util.function.Consumer

class ModuleReach : Module("Reach", "Increases the hit reach", ModuleCategory.GHOST) {

    private val reach = ValueNumber(this, "Reach", 0.0, 3.0, 6.0, 0.1)

    private var origReach: Double? = null

    val eventConsumer = Consumer<Event> { event ->
        if(event is EventUpdateTargetedEntity) {
            when (event.state) {
                EventUpdateTargetedEntity.State.PRE -> {
                    origReach = (mc.gameRenderer as IGameRenderer).tarasande_getReach()
                    (mc.gameRenderer as IGameRenderer).tarasande_setReach(reach.value)
                }

                EventUpdateTargetedEntity.State.POST -> {
                    (mc.gameRenderer as IGameRenderer).tarasande_setReach(origReach!!)
                }
            }
        }
    }

}