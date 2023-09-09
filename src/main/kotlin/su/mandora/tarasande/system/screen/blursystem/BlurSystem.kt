package su.mandora.tarasande.system.screen.blursystem

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gl.Framebuffer
import net.minecraft.client.util.math.MatrixStack
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import su.mandora.tarasande.Manager
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventRender2D
import su.mandora.tarasande.event.impl.EventScreenRender
import su.mandora.tarasande.event.impl.EventSuccessfulLoad
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.screen.blursystem.api.BlurEffect
import su.mandora.tarasande.system.screen.blursystem.impl.BlurBox
import su.mandora.tarasande.system.screen.blursystem.impl.BlurGaussian
import su.mandora.tarasande.system.screen.blursystem.impl.BlurKawase
import su.mandora.tarasande.system.screen.panelsystem.screen.panelscreen.ScreenPanel
import su.mandora.tarasande.util.render.RenderUtil
import su.mandora.tarasande.util.render.framebuffer.SimpleFramebufferWrapped
import su.mandora.tarasande.util.render.shader.Program
import su.mandora.tarasande.util.render.shader.Shader

object ManagerBlur : Manager<Blur>() {

    lateinit var blurEffect: BlurEffect

    private val inGameShapesFramebuffer = SimpleFramebufferWrapped()
    private val screenShapesFramebuffer = SimpleFramebufferWrapped()
    private val cutoutShader = Program(Shader("cutout.frag", GL20.GL_FRAGMENT_SHADER), Shader("default.vert", GL20.GL_VERTEX_SHADER))

    init {
        add(
            BlurBox(),
            BlurGaussian(),
            BlurKawase()
        )

        EventDispatcher.apply {
            add(EventScreenRender::class.java, 1) {
                if (it.state == EventScreenRender.State.PRE && it.screen !is ScreenPanel) {
                    blurScene(it.context.matrices, shapesBuffer = screenShapesFramebuffer)
                }
            }
            add(EventRender2D::class.java, 999) {
                blurScene(it.context.matrices)
            }
            add(EventSuccessfulLoad::class.java, 1001) {
                blurEffect = BlurEffect(this@ManagerBlur)
            }
        }
    }

    fun bind(setViewport: Boolean, screens: Boolean = false) {
        val buffer =
            if (screens)
                screenShapesFramebuffer
            else
                inGameShapesFramebuffer

        buffer.beginWrite(setViewport)
    }

    fun blurScene(matrices: MatrixStack, strength: Int = blurEffect.strength.value.toInt(), shapesBuffer: Framebuffer = inGameShapesFramebuffer, targetBuffer: Framebuffer = mc.framebuffer) {
        if (!RenderSystem.isOnRenderThread()) return

        val cullFace = GL11.glIsEnabled(GL11.GL_CULL_FACE)
        RenderSystem.disableCull()

        val framebuffer = blurEffect(matrices, strength, targetBuffer)

        mc.framebuffer.beginWrite(true)

        cutoutShader.bindProgram()

        cutoutShader["shapes"] = shapesBuffer
        cutoutShader["tex"] = framebuffer
        cutoutShader["resolution"] = floatArrayOf(shapesBuffer.textureWidth.toFloat(), shapesBuffer.textureHeight.toFloat())

        RenderUtil.quad(matrices)

        cutoutShader.unbindProgram()

        shapesBuffer.clear(MinecraftClient.IS_SYSTEM_MAC)
        mc.framebuffer.beginWrite(true)

        if (cullFace) RenderSystem.enableCull()
    }

}

abstract class Blur(val name: String) {

    abstract fun render(matrices: MatrixStack, targetBuffer: Framebuffer, strength: Int): Framebuffer
}
