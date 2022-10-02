package net.tarasandedevelopment.tarasande.module.render

import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.util.shape.VoxelShapes
import net.tarasandedevelopment.tarasande.base.event.Event
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
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

    val hashMap = ConcurrentHashMap<Pair<BlockPos, BlockState>, Long>()

    val eventConsumer = Consumer<Event> { event ->
        when (event) {
            is EventRender3D -> {
                val iterator = hashMap.iterator()
                while (iterator.hasNext()) {
                    val entry = iterator.next()
                    val timeDelta = System.currentTimeMillis() - entry.value
                    if (timeDelta > time.value) {
                        iterator.remove()
                    } else {
                        RenderUtil.blockOutline(event.matrices, entry.key.second.getOutlineShape(mc.world, entry.key.first).let { if (it.isEmpty) VoxelShapes.fullCube() else it }.offset(entry.key.first.x.toDouble(), entry.key.first.y.toDouble(), entry.key.first.z.toDouble()), color.getColor().let { Color(it.red, it.green, it.blue, (it.alpha * (1.0f - timeDelta / time.value)).toInt()) }.rgb)
                    }
                }
            }
        }
    }

}