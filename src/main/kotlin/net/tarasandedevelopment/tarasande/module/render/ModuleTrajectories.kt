package net.tarasandedevelopment.tarasande.module.render

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.BufferRenderer
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexFormat
import net.minecraft.client.render.VertexFormats
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventRender3D
import net.tarasandedevelopment.tarasande.util.math.rotation.RotationUtil
import net.tarasandedevelopment.tarasande.util.player.prediction.projectile.ProjectileUtil
import net.tarasandedevelopment.tarasande.value.ValueBoolean
import org.lwjgl.opengl.GL11

class ModuleTrajectories : Module("Trajectories", "Renders paths of trajectories", ModuleCategory.RENDER) {

    private val predictVelocity = ValueBoolean(this, "Predict velocity", false)

    init {
        registerEvent(EventRender3D::class.java) { event ->
            var stack: ItemStack? = null; Hand.values().forEach { hand ->
            if (ProjectileUtil.projectileItems.any {
                    mc.player?.getStackInHand(hand)?.item?.let { item ->
                        it.isSame(item)
                    } == true
                }) stack = mc.player?.getStackInHand(hand)
        }

            if (stack != null) {
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
                val path = ProjectileUtil.predict(stack!!, RotationUtil.fakeRotation, predictVelocity.value)
                for (vec in path) {
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
}