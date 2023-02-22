package net.tarasandedevelopment.tarasande.feature.tarasandevalue.impl.debug

import net.tarasandedevelopment.tarasande.event.EventDispatcher
import net.tarasandedevelopment.tarasande.event.impl.EventRender3D
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueColor
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.util.player.prediction.PredictionEngine
import net.tarasandedevelopment.tarasande.util.render.RenderUtil

object PlayerMovementPrediction {

    private val enabled = ValueBoolean(this, "Enabled", false)
    private val ticks = ValueNumber(this, "Ticks", 0.0, 20.0, 100.0, 1.0)
    private val everyone = ValueBoolean(this, "Everyone", false)
    private val color = ValueColor(this, "Color", 0.0, 1.0, 1.0, 1.0)

    init {
        EventDispatcher.add(EventRender3D::class.java) {
            if (!enabled.value) return@add

            for (entity in if (everyone.value) mc.world?.players!! else listOf(mc.player))
                if (entity != null)
                    RenderUtil.renderPath(it.matrices, listOf(entity.getLerpedPos(mc.tickDelta)!!, *PredictionEngine.predictState(ticks.value.toInt(), entity).second.toTypedArray()), color.getColor().rgb)
        }
    }
}
