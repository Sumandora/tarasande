package net.tarasandedevelopment.tarasande.module.render

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.BufferRenderer
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexFormat
import net.minecraft.client.render.VertexFormats
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventRender3D
import net.tarasandedevelopment.tarasande.util.player.prediction.PredictionEngine
import net.tarasandedevelopment.tarasande.value.ValueNumber
import org.lwjgl.opengl.GL11

class ModulePrediction : Module("Prediction", "Predicts the local player", ModuleCategory.RENDER) {

    private val ticks = ValueNumber(this, "Ticks", 0.0, 20.0, 100.0, 1.0)

    init {
        registerEvent(EventRender3D::class.java) { event ->
            RenderSystem.enableBlend()
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            RenderSystem.disableCull()
            GL11.glEnable(GL11.GL_LINE_SMOOTH)
            RenderSystem.disableDepthTest()
            event.matrices.push()
            val vec3d = MinecraftClient.getInstance().gameRenderer.camera.pos
            event.matrices.translate(-vec3d.x, -vec3d.y, -vec3d.z)
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
            val bufferBuilder = Tessellator.getInstance().buffer
            bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR)
            val matrix = event.matrices.peek()?.positionMatrix!!
            for (vec in listOf(mc.player?.getLerpedPos(mc.tickDelta)!!, *PredictionEngine.predictState(ticks.value.toInt()).second.toTypedArray())) {
                bufferBuilder.vertex(matrix, vec.x.toFloat(), vec.y.toFloat(), vec.z.toFloat()).color(1f, 1f, 1f, 1f).next()
            }
            BufferRenderer.drawWithShader(bufferBuilder.end())
            event.matrices.pop()
            RenderSystem.enableDepthTest()
            GL11.glDisable(GL11.GL_LINE_SMOOTH)
            RenderSystem.enableCull()
            RenderSystem.disableBlend()
        }
    }
}