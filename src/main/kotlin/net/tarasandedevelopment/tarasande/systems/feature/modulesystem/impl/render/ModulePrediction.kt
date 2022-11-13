package net.tarasandedevelopment.tarasande.systems.feature.modulesystem.impl.render

import net.tarasandedevelopment.tarasande.events.EventRender3D
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.ModuleCategory
import net.tarasandedevelopment.tarasande.util.player.prediction.PredictionEngine
import net.tarasandedevelopment.tarasande.util.render.RenderUtil

class ModulePrediction : Module("Prediction", "Predicts the local player", ModuleCategory.RENDER) {

    private val ticks = ValueNumber(this, "Ticks", 0.0, 20.0, 100.0, 1.0)

    init {
        registerEvent(EventRender3D::class.java) { event ->
            RenderUtil.renderPath(event.matrices, listOf(mc.player?.getLerpedPos(mc.tickDelta)!!, *PredictionEngine.predictState(ticks.value.toInt()).second.toTypedArray()), -1)
        }
    }
}