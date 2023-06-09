package su.mandora.tarasande.system.feature.modulesystem.impl.render

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.registry.Registries
import net.minecraft.util.math.BlockPos
import su.mandora.tarasande.event.impl.EventRender3D
import su.mandora.tarasande.event.impl.EventRenderBlockModel
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueColor
import su.mandora.tarasande.system.base.valuesystem.impl.ValueRegistry
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.util.extension.minecraft.boundingBox
import su.mandora.tarasande.util.render.RenderUtil
import java.util.concurrent.CopyOnWriteArrayList

class ModuleBlockESP : Module("Block ESP", "Highlights blocks through walls", ModuleCategory.RENDER) {

    private val hideBlocks = object : ValueBoolean(this, "Hide blocks", false) {
        override fun onChange(oldValue: Boolean?, newValue: Boolean) = onDisable()
    }
    private val color = ValueColor(this, "Color", 0.0, 1.0, 1.0, 1.0, isEnabled = { !hideBlocks.value })
    private val blocks = object : ValueRegistry<Block>(this, "Blocks", Registries.BLOCK, true, Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.ENDER_CHEST) {
        override fun onAdd(key: Block) = onDisable()
        override fun onRemove(key: Block) = onDisable()
        override fun filter(key: Block) = !key.defaultState.getOutlineShape(mc.world, BlockPos.ORIGIN).isEmpty
        override fun getTranslationKey(key: Any?) = (key as Block).translationKey
    }

    var list = CopyOnWriteArrayList<Pair<BlockPos, BlockState>>()

    override fun onEnable() {
        mc.worldRenderer.reload()
    }

    override fun onDisable() {
        if (!enabled.value)
            return
        list = CopyOnWriteArrayList()
        mc.worldRenderer.reload()
    }

    init {
        registerEvent(EventRenderBlockModel::class.java) { event ->
            if (!blocks.isSelected(event.state.block)) {
                if (hideBlocks.value) {
                    event.cancelled = true
                }
            } else if (!hideBlocks.value) {
                if (list.none { it.first == event.pos })
                    list.add(Pair(BlockPos(event.pos), event.state))
            }
        }

        registerEvent(EventRender3D::class.java) { event ->
            list.removeIf { mc.world?.getBlockState(it.first)?.block != it.second.block }
            for (pair in list) {
                val pos = pair.first
                val shape = pair.second.getOutlineShape(mc.world, pos)?.offset(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble())
                if (shape != null) {
                    RenderUtil.blockOutline(event.matrices, shape.boundingBox(), color.getColor().rgb)
                } else {
                    list.remove(pair)
                }
            }
        }
    }
}