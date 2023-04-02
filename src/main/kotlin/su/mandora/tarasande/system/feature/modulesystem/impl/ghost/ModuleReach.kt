package su.mandora.tarasande.system.feature.modulesystem.impl.ghost

import su.mandora.tarasande.event.impl.EventUpdateTargetedEntity
import su.mandora.tarasande.injection.accessor.IGameRenderer
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory

class ModuleReach : Module("Reach", "Increases the hit reach", ModuleCategory.GHOST) {

    private val reach = ValueNumber(this, "Reach", 0.1, 3.0, 6.0, 0.1)

    private var origReach: Double? = null

    init {
        registerEvent(EventUpdateTargetedEntity::class.java) { event ->
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
