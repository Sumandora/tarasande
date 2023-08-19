package su.mandora.tarasande.system.feature.modulesystem.impl.render

import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventRender3D
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueColor
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.util.extension.javaruntime.withAlpha
import su.mandora.tarasande.util.extension.minecraft.math.boundingBox
import su.mandora.tarasande.util.render.RenderUtil
import java.util.concurrent.CopyOnWriteArrayList

class ModuleBlockChangeTracker : Module("Block change tracker", "Highlights recently changed blocks", ModuleCategory.RENDER) {

    private val color = ValueColor(this, "Color", 0.0, 1.0, 1.0, 1.0)
    private val time = ValueNumber(this, "Time", 0.0, 1000.0, 10000.0, 500.0)

    val changes = CopyOnWriteArrayList<Triple<BlockPos, BlockState, Long>>()

    init {
        EventDispatcher.add(EventRender3D::class.java) { event ->
            if(event.state != EventRender3D.State.POST) return@add

            for (change in changes) {
                val timeDelta = System.currentTimeMillis() - change.third
                if (timeDelta > time.value) {
                    changes.remove(change)
                } else {
                    RenderUtil.blockOutline(event.matrices, change.second.getOutlineShape(mc.world, change.first).boundingBox().offset(change.first.x.toDouble(), change.first.y.toDouble(), change.first.z.toDouble()), color.getColor().withAlpha((color.getColor().alpha * (1F - timeDelta / time.value)).toInt()).rgb)
                }
            }
        }
    }
}