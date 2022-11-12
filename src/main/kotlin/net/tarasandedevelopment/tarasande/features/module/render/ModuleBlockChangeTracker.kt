package net.tarasandedevelopment.tarasande.features.module.render

import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.util.shape.VoxelShapes
import net.tarasandedevelopment.tarasande.base.features.module.Module
import net.tarasandedevelopment.tarasande.base.features.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventRender3D
import net.tarasandedevelopment.tarasande.util.extension.withAlpha
import net.tarasandedevelopment.tarasande.util.render.RenderUtilimport net.tarasandedevelopment.tarasande.value.impl.ValueColor
import net.tarasandedevelopment.tarasande.value.impl.ValueNumber
import java.util.concurrent.CopyOnWriteArrayList

class ModuleBlockChangeTracker : Module("Block change tracker", "Tracks block changes", ModuleCategory.RENDER) {

    private val color = ValueColor(this, "Color", 0.0f, 1.0f, 1.0f, 1.0f)
    private val time = ValueNumber(this, "Time", 0.0, 1000.0, 10000.0, 500.0)

    val changes = CopyOnWriteArrayList<Triple<BlockPos, BlockState, Long>>()

    init {
        registerEvent(EventRender3D::class.java) { event ->
            for (change in changes) {
                val timeDelta = System.currentTimeMillis() - change.third
                if (timeDelta > time.value) {
                    changes.remove(change)
                } else {
                    RenderUtil.blockOutline(event.matrices, change.second.getOutlineShape(mc.world, change.first).let { if (it.isEmpty) VoxelShapes.fullCube() else it }.offset(change.first.x.toDouble(), change.first.y.toDouble(), change.first.z.toDouble()), color.getColor().withAlpha((color.getColor().alpha * (1.0f - timeDelta / time.value)).toInt()).rgb)
                }
            }
        }
    }
}