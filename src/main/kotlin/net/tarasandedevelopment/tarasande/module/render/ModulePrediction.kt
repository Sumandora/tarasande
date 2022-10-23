package net.tarasandedevelopment.tarasande.module.render

import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventRender3D
import net.tarasandedevelopment.tarasande.util.player.prediction.PredictionEngine
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import net.tarasandedevelopment.tarasande.value.ValueNumber

class ModulePrediction : Module("Prediction", "Predicts the local player", ModuleCategory.RENDER) {

    private val ticks = ValueNumber(this, "Ticks", 0.0, 20.0, 100.0, 1.0)

    init {
        registerEvent(EventRender3D::class.java) { event ->
            RenderUtil.renderPath(event.matrices, listOf(mc.player?.getLerpedPos(mc.tickDelta)!!, *PredictionEngine.predictState(ticks.value.toInt()).second.toTypedArray()), -1)
        }
    }
}