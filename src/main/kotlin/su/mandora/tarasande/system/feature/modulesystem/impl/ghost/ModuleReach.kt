package su.mandora.tarasande.system.feature.modulesystem.impl.ghost

import su.mandora.tarasande.event.impl.EventUpdateTargetedEntity
import su.mandora.tarasande.injection.accessor.IGameRenderer
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.util.DEFAULT_BLOCK_REACH
import su.mandora.tarasande.util.DEFAULT_REACH
import su.mandora.tarasande.util.maxReach

class ModuleReach : Module("Reach", "Increases the hit reach", ModuleCategory.GHOST) {

    private val reach = ValueNumber(this, "Reach", 0.1, DEFAULT_REACH, maxReach, 0.1)
    private val modifyBlockReach = ValueBoolean(this, "Modify block reach", false)
    private val blockReach = ValueNumber(this, "Block reach", 0.1, DEFAULT_BLOCK_REACH, maxReach, 0.1, isEnabled = { modifyBlockReach.value })

    private var origReach: Double? = null
    private var origBlockReach: Double? = null

    init {
        registerEvent(EventUpdateTargetedEntity::class.java) { event ->
            when (event.state) {
                EventUpdateTargetedEntity.State.PRE -> {
                    val accessor = mc.gameRenderer as IGameRenderer
                    if(!accessor.tarasande_isSelfInflicted()) {
                        origReach = accessor.tarasande_getReach()
                        accessor.tarasande_setReach(reach.value)
                        if(modifyBlockReach.value) {
                            origBlockReach = accessor.tarasande_getBlockReach()
                            accessor.tarasande_setBlockReach(blockReach.value)
                        }
                    }
                }

                EventUpdateTargetedEntity.State.POST -> {
                    val accessor = mc.gameRenderer as IGameRenderer
                    if(!accessor.tarasande_isSelfInflicted()) {
                        if(modifyBlockReach.value)
                            accessor.tarasande_setBlockReach(origBlockReach!!)
                        accessor.tarasande_setReach(origReach!!)
                    }
                }
            }
        }
    }
}
