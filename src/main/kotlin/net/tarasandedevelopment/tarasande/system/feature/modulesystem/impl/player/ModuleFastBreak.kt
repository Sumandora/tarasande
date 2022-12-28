package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.player

import net.minecraft.item.ToolItem
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult
import net.tarasandedevelopment.tarasande.event.EventUpdate
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueMode
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory

class ModuleFastBreak : Module("Fast break", "Makes blocks break faster", ModuleCategory.PLAYER) {

    private val mode = ValueMode(this, "Mode", false, "Speed up", "Stop early")
    private val speedUpMode = object : ValueMode(this, "Speed up mode", false, "Addition", "Multiplication") {
        override fun isEnabled() = mode.isSelected(0)
    }
    private val speed = ValueNumber(this, "Speed", 0.0, 0.5, 1.0, 0.01)
    private val onlyWhenHoldingAppropriateTool = ValueBoolean(this, "Only when holding appropriate tool", true)

    init {
        registerEvent(EventUpdate::class.java) { event ->
            if (event.state == EventUpdate.State.PRE) {
                val item = mc.player?.mainHandStack?.item
                var suitableForBlock = false
                if (mc.crosshairTarget != null && mc.crosshairTarget?.type == HitResult.Type.BLOCK && mc.crosshairTarget is BlockHitResult)
                    if (item is ToolItem && item.isSuitableFor(mc.world?.getBlockState((mc.crosshairTarget as BlockHitResult).blockPos)))
                        suitableForBlock = true
                if(!onlyWhenHoldingAppropriateTool.value || suitableForBlock)
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
                                mc.interactionManager!!.currentBreakingProgress = 1.0F
                        }
                    }
            }
        }
    }
}
