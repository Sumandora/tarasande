package su.mandora.tarasande.system.feature.modulesystem.impl.render

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.client.render.*
import net.minecraft.registry.Registries
import net.minecraft.util.math.BlockPos
import org.lwjgl.opengl.GL11
import su.mandora.tarasande.event.impl.EventRender3D
import su.mandora.tarasande.event.impl.EventRenderBlockModel
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueColor
import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode
import su.mandora.tarasande.system.base.valuesystem.impl.ValueRegistry
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.util.extension.minecraft.math.unaryMinus
import java.util.concurrent.CopyOnWriteArrayList

class ModuleBlockESP : Module("Block ESP", "Highlights blocks through walls", ModuleCategory.RENDER) {

    private val hideBlocks = object : ValueBoolean(this, "Hide blocks", false) {
        override fun onChange(oldValue: Boolean?, newValue: Boolean) {
            if(enabled.value)
                onDisable()
        }
    }
    private val color = ValueColor(this, "Color", 0.0, 1.0, 1.0, 1.0, isEnabled = { !hideBlocks.value })
    private val blocks = object : ValueRegistry<Block>(this, "Blocks", Registries.BLOCK, true, Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.ENDER_CHEST) {
        override fun onAdd(key: Block) {
            if(enabled.value)
                onDisable()
        }
        override fun onRemove(key: Block) {
            if(enabled.value)
                onDisable()
        }
        override fun filter(key: Block) = !key.defaultState.getOutlineShape(mc.world, BlockPos.ORIGIN).isEmpty
        override fun getTranslationKey(key: Any?) = (key as Block).translationKey
    }
    private val freestandingOnly = object : ValueMode(this, "Freestanding only", false, "Off", "On", "Strict") {
        override fun onChange(index: Int, oldSelected: Boolean, newSelected: Boolean) {
            if(enabled.value)
                onDisable()
        }
    }

    var list = CopyOnWriteArrayList<Pair<BlockPos, BlockState>>()

    override fun onEnable() {
        mc.worldRenderer.reload()
    }

    override fun onDisable() {
        list = CopyOnWriteArrayList()
        mc.worldRenderer.reload()
    }

    private fun BlockPos.allSurroundings(): ArrayList<BlockPos> {
        val list = ArrayList<BlockPos>()
        for (x in -1..1 step 2) list.add(add(x, 0, 0))
        for (y in -1..1) list.add(add(0, y, 0))
        for (z in -1..1 step 2) list.add(add(0, 0, z))
        return list
    }

    init {
        registerEvent(EventRenderBlockModel::class.java) { event ->
            if (!blocks.isSelected(event.state.block) || (!freestandingOnly.isSelected(0) && event.pos.allSurroundings().none { pos -> mc.world!!.getBlockState(pos).let {
                when {
                    freestandingOnly.isSelected(1) -> it.getCullingShape(mc.world, pos).isEmpty
                    freestandingOnly.isSelected(2) -> it.isAir
                    else -> false
                }
                } })) {
                if (hideBlocks.value) {
                    event.cancelled = true
                }
            } else if (!hideBlocks.value) {
                if (list.none { it.first == event.pos })
                    list.add(Pair(BlockPos(event.pos), event.state))
            }
        }

        registerEvent(EventRender3D::class.java) { event ->
            if(event.state != EventRender3D.State.POST) return@registerEvent

            list.removeIf { mc.world?.getBlockState(it.first)?.block != it.second.block }
            if(list.isNotEmpty()) {
                val matrix = event.matrices.peek().positionMatrix

                RenderSystem.enableBlend()
                RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
                RenderSystem.disableDepthTest()

                val bufferBuilder = Tessellator.getInstance().buffer
                RenderSystem.setShader { GameRenderer.getPositionProgram() }
                val color = color.getColor()
                RenderSystem.setShaderColor(color.red / 255F, color.green / 255F, color.blue / 255F, color.alpha / 255F)
                bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION)

                val vec3d = -mc.gameRenderer.camera.pos
                for (pair in list) {
                    val pos = pair.first
                    val shape = pair.second.getOutlineShape(mc.world, pos)
                    if (shape != null) {
                        val box = shape.offset(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble()).offset(vec3d.x, vec3d.y, vec3d.z).boundingBox
                        val minX = box.minX.toFloat()
                        val maxX = box.maxX.toFloat()
                        val minY = box.minY.toFloat()
                        val maxY = box.maxY.toFloat()
                        val minZ = box.minZ.toFloat()
                        val maxZ = box.maxZ.toFloat()

                        bufferBuilder.vertex(matrix, minX, minY, minZ).next()
                        bufferBuilder.vertex(matrix, maxX, minY, minZ).next()
                        bufferBuilder.vertex(matrix, maxX, minY, maxZ).next()
                        bufferBuilder.vertex(matrix, minX, minY, maxZ).next()

                        bufferBuilder.vertex(matrix, minX, maxY, minZ).next()
                        bufferBuilder.vertex(matrix, minX, maxY, maxZ).next()
                        bufferBuilder.vertex(matrix, maxX, maxY, maxZ).next()
                        bufferBuilder.vertex(matrix, maxX, maxY, minZ).next()

                        bufferBuilder.vertex(matrix, minX, minY, minZ).next()
                        bufferBuilder.vertex(matrix, minX, maxY, minZ).next()
                        bufferBuilder.vertex(matrix, maxX, maxY, minZ).next()
                        bufferBuilder.vertex(matrix, maxX, minY, minZ).next()

                        bufferBuilder.vertex(matrix, maxX, minY, minZ).next()
                        bufferBuilder.vertex(matrix, maxX, maxY, minZ).next()
                        bufferBuilder.vertex(matrix, maxX, maxY, maxZ).next()
                        bufferBuilder.vertex(matrix, maxX, minY, maxZ).next()

                        bufferBuilder.vertex(matrix, minX, minY, maxZ).next()
                        bufferBuilder.vertex(matrix, maxX, minY, maxZ).next()
                        bufferBuilder.vertex(matrix, maxX, maxY, maxZ).next()
                        bufferBuilder.vertex(matrix, minX, maxY, maxZ).next()

                        bufferBuilder.vertex(matrix, minX, minY, minZ).next()
                        bufferBuilder.vertex(matrix, minX, minY, maxZ).next()
                        bufferBuilder.vertex(matrix, minX, maxY, maxZ).next()
                        bufferBuilder.vertex(matrix, minX, maxY, minZ).next()
                    } else {
                        list.remove(pair)
                    }
                }

                BufferRenderer.drawWithGlobalProgram(bufferBuilder.end())

                RenderSystem.enableDepthTest()
                RenderSystem.enableBlend()
            }
        }
    }
}