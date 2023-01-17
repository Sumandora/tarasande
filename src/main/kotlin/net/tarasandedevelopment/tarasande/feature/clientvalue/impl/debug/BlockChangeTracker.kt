package net.tarasandedevelopment.tarasande.feature.clientvalue.impl.debug

import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos
import net.tarasandedevelopment.tarasande.event.EventRender3D
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueColor
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.util.extension.javaruntime.withAlpha
import net.tarasandedevelopment.tarasande.util.extension.minecraft.boundingBox
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import su.mandora.event.EventDispatcher
import java.util.concurrent.CopyOnWriteArrayList

class BlockChangeTracker {

    val enabled = ValueBoolean(this, "Enabled", false)
    private val color = ValueColor(this, "Color", 0.0, 1.0, 1.0, 1.0)
    private val time = ValueNumber(this, "Time", 0.0, 1000.0, 10000.0, 500.0)

    val changes = CopyOnWriteArrayList<Triple<BlockPos, BlockState, Long>>()

    init {
        EventDispatcher.add(EventRender3D::class.java) { event ->
            for (change in changes) {
                val timeDelta = System.currentTimeMillis() - change.third
                if (timeDelta > time.value) {
                    changes.remove(change)
                } else {
                    RenderUtil.blockOutline(event.matrices, change.second.getOutlineShape(mc.world, change.first).boundingBox().offset(change.first.x.toDouble(), change.first.y.toDouble(), change.first.z.toDouble()), color.getColor().withAlpha((color.getColor().alpha * (1.0F - timeDelta / time.value)).toInt()).rgb)
                }
            }
        }
    }
}