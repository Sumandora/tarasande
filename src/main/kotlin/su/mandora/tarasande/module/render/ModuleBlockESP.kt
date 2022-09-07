package su.mandora.tarasande.module.render

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventRender3D
import su.mandora.tarasande.event.EventRenderBlockModel
import su.mandora.tarasande.util.render.RenderUtil
import su.mandora.tarasande.value.ValueBoolean
import su.mandora.tarasande.value.ValueColor
import su.mandora.tarasande.value.ValueRegistry
import java.util.concurrent.CopyOnWriteArrayList
import java.util.function.Consumer

class ModuleBlockESP : Module("Block ESP", "Highlights blocks through walls", ModuleCategory.RENDER) {

    private val hideBlocks = object : ValueBoolean(this, "Hide blocks", false) {
        override fun onChange() = onDisable()
    }
    private val color = object : ValueColor(this, "Color", 0.0f, 1.0f, 1.0f, 1.0f) {
        override fun isEnabled() = !hideBlocks.value
    }
    private val blocks = object : ValueRegistry<Block>(this, "Blocks", Registry.BLOCK) {
        override fun onChange() = onDisable()
        override fun filter(key: Block) = key != Blocks.AIR
        override fun getTranslationKey(key: Any?) = (key as Block).translationKey
    }

    private val list = CopyOnWriteArrayList<Pair<BlockPos, BlockState>>()

    override fun onEnable() {
        mc.worldRenderer.reload()
    }

    override fun onDisable() {
        if (!enabled)
            return
        list.clear()
        mc.worldRenderer.reload()
    }

    val eventConsumer = Consumer<Event> { event ->
        when (event) {
            is EventRenderBlockModel -> {
                if (!blocks.list.contains(event.state.block)) {
                    if (hideBlocks.value) {
                        event.cancelled = true
                    }
                } else if (!hideBlocks.value) {
                    if (list.none { it.first == event.pos })
                        list.add(Pair(BlockPos(event.pos), event.state))
                }
            }

            is EventRender3D -> {
                list.removeIf { mc.world?.getBlockState(it.first)?.block != it.second.block }
                for (pair in list) {
                    val pos = pair.first
                    val shape = pair.second.getOutlineShape(mc.world, pos)?.offset(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble())
                    if (shape != null) {
                        RenderUtil.blockOutline(event.matrices, shape, color.getColor().rgb)
                    } else {
                        list.remove(pair)
                    }
                }
            }
        }
    }

}