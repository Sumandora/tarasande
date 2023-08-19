package su.mandora.tarasande.system.feature.modulesystem.impl.ghost

import net.minecraft.item.BlockItem
import su.mandora.tarasande.event.impl.EventTick
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.util.DEFAULT_PLACE_DELAY
import su.mandora.tarasande.util.player.PlayerUtil

class ModuleFastPlace : Module("Fast place", "Speeds up block placements", ModuleCategory.GHOST) {

    private val maximumDelay = ValueNumber(this, "Maximum delay", 0.0, 0.0, DEFAULT_PLACE_DELAY.toDouble(), 1.0)
    private val onlyWhenPlacing = ValueBoolean(this, "Only when placing", true)

    init {
        registerEvent(EventTick::class.java) { event ->
            if (event.state == EventTick.State.PRE) {
                if(mc.player == null)
                    return@registerEvent
                val usedHand = PlayerUtil.getUsedHand() ?: return@registerEvent
                if(!onlyWhenPlacing.value || mc.player?.getStackInHand(usedHand)?.item is BlockItem)
                    mc.itemUseCooldown = mc.itemUseCooldown.coerceAtMost(maximumDelay.value.toInt())

            }
        }
    }
}
