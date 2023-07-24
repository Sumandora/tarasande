package su.mandora.tarasande.system.feature.modulesystem.impl.player

import net.minecraft.item.ToolItem
import net.minecraft.util.hit.BlockHitResult
import su.mandora.tarasande.event.impl.EventUpdate
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.util.extension.minecraft.isBlockHitResult

class ModuleFastBreak : Module("Fast break", "Makes blocks break faster", ModuleCategory.PLAYER) {

    private val mode = ValueMode(this, "Mode", false, "Speed up", "Stop early")
    private val speedUpMode = ValueMode(this, "Speed up mode", false, "Addition", "Multiplication", isEnabled = { mode.isSelected(0) })
    private val speed = ValueNumber(this, "Speed", 0.0, 0.5, 1.0, 0.01)
    private val onlyWhenHoldingAppropriateTool = ValueBoolean(this, "Only when holding appropriate tool", true)

    init {
        registerEvent(EventUpdate::class.java) { event ->
            if (event.state == EventUpdate.State.PRE) {
                val item = mc.player?.mainHandStack?.item
                var suitableForBlock = false
                if (mc.crosshairTarget.isBlockHitResult())
                    if (item is ToolItem && item.isSuitableFor(mc.world?.getBlockState((mc.crosshairTarget as BlockHitResult).blockPos)))
                        suitableForBlock = true
                if (!onlyWhenHoldingAppropriateTool.value || suitableForBlock)
                    when {
                        mode.isSelected(0) -> {
                            if (mc.interactionManager!!.currentBreakingProgress > 0.0)
                                when {
                                    speedUpMode.isSelected(0) -> mc.interactionManager!!.currentBreakingProgress += speed.value.toFloat()
                                    speedUpMode.isSelected(1) -> mc.interactionManager!!.currentBreakingProgress *= (1.0 + speed.value).toFloat()
                                }
                        }

                        mode.isSelected(1) -> {
                            if (mc.interactionManager!!.currentBreakingProgress >= speed.value)
                                mc.interactionManager!!.currentBreakingProgress = 1F
                        }
                    }
            }
        }
    }
}
