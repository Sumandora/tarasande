package net.tarasandedevelopment.tarasande.module.render

import net.minecraft.util.shape.VoxelShapes
import net.tarasandedevelopment.tarasande.base.event.Event
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventBlockChange
import net.tarasandedevelopment.tarasande.event.EventRender3D
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import net.tarasandedevelopment.tarasande.value.ValueColor
import net.tarasandedevelopment.tarasande.value.ValueNumber
import java.awt.Color
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Consumer

class ModuleBlockChangeTracker : Module("Block change tracker", "Tracks block changes", ModuleCategory.RENDER) {

    private val color = ValueColor(this, "Color", 0.0f, 1.0f, 1.0f, 1.0f)
    private val time = ValueNumber(this, "Time", 0.0, 1000.0, 10000.0, 500.0)

    private val hashMap = ConcurrentHashMap<EventBlockChange, Long>()

    val eventConsumer = Consumer<Event> { event ->
        when (event) {
            is EventBlockChange -> {
                if (mc.world?.getBlockState(event.pos)?.block?.equals(event.state.block) != true)
                    hashMap[event] = System.currentTimeMillis()
            }

            is EventRender3D -> {
                val iterator = hashMap.iterator()
                while (iterator.hasNext()) {
                    val entry = iterator.next()
                    val timeDelta = System.currentTimeMillis() - entry.value
                    if (timeDelta > time.value) {
                        iterator.remove()
                    } else {
                        RenderUtil.blockOutline(event.matrices, entry.key.state.getOutlineShape(mc.world, entry.key.pos).let { if (it.isEmpty) VoxelShapes.fullCube() else it }.offset(entry.key.pos.x.toDouble(), entry.key.pos.y.toDouble(), entry.key.pos.z.toDouble()), color.getColor().let { Color(it.red, it.green, it.blue, (it.alpha * (1.0f - timeDelta / time.value)).toInt()) }.rgb)
                    }
                }
            }
        }
    }

}