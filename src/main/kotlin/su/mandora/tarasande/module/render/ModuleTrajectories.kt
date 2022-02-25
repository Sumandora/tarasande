package su.mandora.tarasande.module.render

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.BufferRenderer
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexFormat
import net.minecraft.client.render.VertexFormats
import net.minecraft.item.BowItem
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand
import net.minecraft.util.math.Vec3d
import org.lwjgl.opengl.GL11
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventRender3D
import java.util.function.Consumer
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.tan

class ModuleTrajectories : Module("Trajectories", "Renders paths of trajectories", ModuleCategory.RENDER) {

    private fun formula(angle: Double, x: Double, v: Double, g: Double) = v * x * sin(angle) - 1 / 2 * g * (x * x)

    val eventConsumer = Consumer<Event> { event ->
        if(event is EventRender3D) {
            var bowItem: ItemStack? = null
            if(mc.player?.getStackInHand(Hand.MAIN_HAND)?.item is BowItem)
                bowItem = mc.player?.getStackInHand(Hand.MAIN_HAND)
            else if (mc.player?.getStackInHand(Hand.OFF_HAND)?.item is BowItem)
                bowItem = mc.player?.getStackInHand(Hand.OFF_HAND)
            if(bowItem != null) {
                val velocity = BowItem.getPullProgress(if(mc.player?.isUsingItem!!) mc.player?.itemUseTime!! else bowItem.maxUseTime).toDouble()
                val gravity = 0.006
                GL11.glEnable(GL11.GL_BLEND)
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
                GL11.glDisable(GL11.GL_CULL_FACE)
                GL11.glEnable(GL11.GL_LINE_SMOOTH)
                GL11.glEnable(GL11.GL_DEPTH_TEST)
                GL11.glDepthMask(true)
                event.matrices.push()
                val vec3d = MinecraftClient.getInstance().gameRenderer.camera.pos
                event.matrices.translate(-vec3d.x, -vec3d.y, -vec3d.z)
                val bufferBuilder = Tessellator.getInstance().buffer
                RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
                bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR)
                val matrix = event.matrices.peek()?.positionMatrix!!
                for (distance in 0..150) {
                    val rad = Math.toRadians(mc.player?.yaw?.toDouble()!! + 95)
                    val forward = Vec3d(cos(rad) * distance, 0.0, sin(rad) * distance)
//                    val height = formula(45.0, distance.toDouble(), velocity, gravity)
                    val height = formula(45.0, distance.toDouble(), velocity, gravity)
                    val pos = mc.player?.eyePos?.add(0.0, -0.1, 0.0)?.add(forward.x, height, forward.z)
                    bufferBuilder.vertex(matrix, pos?.x?.toFloat()!!, pos.y.toFloat(), pos.z.toFloat()).color(1f,1f,1f,1f).next()
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