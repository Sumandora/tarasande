package su.mandora.tarasande.feature.tarasandevalue.impl.debug

import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventRender3D
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueColor
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.util.player.prediction.PredictionEngine
import su.mandora.tarasande.util.render.RenderUtil

object PlayerMovementPrediction {

    private val enabled = ValueBoolean(this, "Enabled", false)
    private val ticks = ValueNumber(this, "Ticks", 0.0, 20.0, 100.0, 1.0)
    private val everyone = ValueBoolean(this, "Everyone", false)
    private val color = ValueColor(this, "Color", 0.0, 1.0, 1.0, 1.0)

    init {
        EventDispatcher.add(EventRender3D::class.java) { event ->
            if(event.state != EventRender3D.State.POST) return@add

            if (!enabled.value) return@add

            for (entity in if (everyone.value) mc.world?.players!! else listOf(mc.player))
                if (entity != null)
                    RenderUtil.renderPath(event.matrices, listOf(entity.getLerpedPos(mc.tickDelta)!!, *PredictionEngine.predictState(ticks.value.toInt(), entity).second.toTypedArray()), color.getColor().rgb)
        }
    }
}
