package su.mandora.tarasande.module.render

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.BufferRenderer
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexFormat
import net.minecraft.client.render.VertexFormats
import net.minecraft.item.ArrowItem
import net.minecraft.item.BowItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.Hand
import net.minecraft.util.math.Vec3d
import org.lwjgl.opengl.GL11
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventRender3D
import su.mandora.tarasande.mixin.accessor.IEntity
import su.mandora.tarasande.mixin.accessor.IWorld
import java.util.*
import java.util.function.Consumer

class ModuleTrajectories : Module("Trajectories", "Renders paths of trajectories", ModuleCategory.RENDER) {

    private fun predict(v: Double): ArrayList<Vec3d> {
        (mc.world as IWorld).setIsClient(false)
        val path = ArrayList<Vec3d>()
        val persistentProjectileEntity = (Items.ARROW as ArrowItem).createArrow(mc.world, ItemStack(Items.ARROW), mc.player)
        (persistentProjectileEntity as IEntity).setRandom(object : Random() {
            override fun next(bits: Int): Int {
                return 0
            }

            override fun nextGaussian(): Double {
                return 0.0
            }
        })
        persistentProjectileEntity.setVelocity(mc.player, mc.player?.pitch!!, mc.player?.yaw!!, 0.0f, (v * 3.0).toFloat(), 1.0f)
        while (true) {
            persistentProjectileEntity.tick()
            if (persistentProjectileEntity.pos.let { it.y < mc.world?.bottomY!! || it == path.lastOrNull() }) break
            path.add(persistentProjectileEntity.pos)
        }
        (mc.world as IWorld).setIsClient(true)
        return path
    }

    val eventConsumer = Consumer<Event> { event ->
        if (event is EventRender3D) {
            var bowItem: ItemStack? = null
            if (mc.player?.getStackInHand(Hand.MAIN_HAND)?.item is BowItem)
                bowItem = mc.player?.getStackInHand(Hand.MAIN_HAND)
            else if (mc.player?.getStackInHand(Hand.OFF_HAND)?.item is BowItem)
                bowItem = mc.player?.getStackInHand(Hand.OFF_HAND)

            if (bowItem != null) {
                val velocity = BowItem.getPullProgress(if (mc.player?.isUsingItem!!) mc.player?.itemUseTime!! else bowItem.maxUseTime).toDouble()
                GL11.glEnable(GL11.GL_BLEND)
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
                GL11.glDisable(GL11.GL_CULL_FACE)
                GL11.glEnable(GL11.GL_LINE_SMOOTH)
                GL11.glEnable(GL11.GL_DEPTH_TEST)
                GL11.glDepthMask(true)
                event.matrices.push()
                val vec3d = MinecraftClient.getInstance().gameRenderer.camera.pos
                event.matrices.translate(-vec3d.x, -vec3d.y, -vec3d.z)
                RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
                val bufferBuilder = Tessellator.getInstance().buffer
                bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR)
                val matrix = event.matrices.peek()?.positionMatrix!!
                val path = predict(velocity)
                for (vec in path) {
                    bufferBuilder.vertex(matrix, vec.x.toFloat(), vec.y.toFloat(), vec.z.toFloat()).color(1f, 1f, 1f, 1f).next()
                }
                bufferBuilder.end()
                BufferRenderer.draw(bufferBuilder)
                event.matrices.pop()
                GL11.glDisable(GL11.GL_BLEND)
                GL11.glDisable(GL11.GL_LINE_SMOOTH)
            }
        }
    }
}