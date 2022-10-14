package net.tarasandedevelopment.tarasande.module.player

import net.minecraft.util.hit.BlockHitResult
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventUpdate
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil

class ModuleAutoTool : Module("Auto tool", "Selects the best tool for breaking a block", ModuleCategory.PLAYER) {

    init {
        registerEvent(EventUpdate::class.java) { event ->
            if (event.state == EventUpdate.State.PRE) {
                if (!mc.interactionManager?.isBreakingBlock!!)
                    return@registerEvent
                if (mc.crosshairTarget !is BlockHitResult)
                    return@registerEvent
                val blockPos = (mc.crosshairTarget as BlockHitResult).blockPos
                val pair = PlayerUtil.getBreakSpeed(blockPos)

                if (pair.second == mc.player?.inventory?.selectedSlot)
                    return@registerEvent

                val currentSpeed = PlayerUtil.getBreakSpeed(blockPos, mc.player?.inventory?.selectedSlot ?: return@registerEvent)
                if (currentSpeed == pair.first)
                    return@registerEvent

                val bestTool = pair.second
                if (bestTool == -1)
                    return@registerEvent
                mc.player?.inventory?.selectedSlot = bestTool
            }
        }
    }
}