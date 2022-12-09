package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.render

import net.tarasandedevelopment.tarasande.event.EventRender3D
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory
import net.tarasandedevelopment.tarasande.util.player.prediction.PredictionEngine
import net.tarasandedevelopment.tarasande.util.render.RenderUtil

class ModulePrediction : Module("Prediction", "Predicts the local player", ModuleCategory.RENDER) {

    private val ticks = ValueNumber(this, "Ticks", 0.0, 20.0, 100.0, 1.0)
    private val everyone = ValueBoolean(this, "Everyone", false)

    init {
        registerEvent(EventRender3D::class.java) { event ->
            for(entity in if(everyone.value) mc.world?.players!! else listOf(mc.player)) {
                if(entity != null)
                    RenderUtil.renderPath(event.matrices, listOf(entity.getLerpedPos(mc.tickDelta)!!, *PredictionEngine.predictState(ticks.value.toInt(), entity).second.toTypedArray()), -1)
            }
        }
    }
}