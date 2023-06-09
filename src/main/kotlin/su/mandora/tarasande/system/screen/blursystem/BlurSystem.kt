package su.mandora.tarasande.system.screen.blursystem

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gl.Framebuffer
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL20
import su.mandora.tarasande.Manager
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventRender2D
import su.mandora.tarasande.event.impl.EventScreenRender
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.screen.blursystem.impl.BlurBox
import su.mandora.tarasande.system.screen.blursystem.impl.BlurGaussian
import su.mandora.tarasande.system.screen.blursystem.impl.BlurKawase
import su.mandora.tarasande.system.screen.panelsystem.screen.panelscreen.ScreenPanel
import su.mandora.tarasande.util.render.framebuffer.SimpleFramebufferWrapped
import su.mandora.tarasande.util.render.shader.Program
import su.mandora.tarasande.util.render.shader.Shader

object ManagerBlur : Manager<Blur>() {

    lateinit var selected: Blur
    val mode: ValueMode
    val strength = ValueNumber(this, "Blur strength", 1.0, 1.0, 20.0, 1.0, exceed = false)

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
                if (it.state == EventScreenRender.State.PRE && it.screen !is ScreenPanel)
                    blurScene(shapesBuffer = screenShapesFramebuffer)
            }
            add(EventRender2D::class.java, 999) {
                blurScene()
            }
        }

        mode = object : ValueMode(this, "Blur mode", false, *list.map { it.name }.toTypedArray()) {
            override fun onChange(index: Int, oldSelected: Boolean, newSelected: Boolean) {
                if (!oldSelected && newSelected)
                    selected = list[index]
            }
        }

        mode.select(2) // Select the best blur as default
    }

    fun bind(setViewport: Boolean, screens: Boolean = false) {
        val buffer =
            if (screens)
                screenShapesFramebuffer
            else
                inGameShapesFramebuffer

        buffer.beginWrite(setViewport)
    }

    fun blurScene(strength: Int? = null, shapesBuffer: Framebuffer = inGameShapesFramebuffer, targetBuffer: Framebuffer = mc.framebuffer) {
        if (!RenderSystem.isOnRenderThread()) return

        GL11.glPushMatrix()
        val texture2D = GL11.glIsEnabled(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        val cullFace = GL11.glIsEnabled(GL11.GL_CULL_FACE)
        GL11.glDisable(GL11.GL_CULL_FACE)

        val activeTexture = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE)

        val framebuffer = selected.render(targetBuffer, strength ?: this.strength.value.toInt())

        mc.framebuffer.beginWrite(MinecraftClient.IS_SYSTEM_MAC)

        val prevShader = cutoutShader.bindProgram()
        GL20.glUniform1i(cutoutShader.getUniformLocation("shapes"), 0)
        GL13.glActiveTexture(GL13.GL_TEXTURE0)
        val texture0 = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D)
        GlStateManager._bindTexture(shapesBuffer.colorAttachment)

        GL20.glUniform1i(cutoutShader.getUniformLocation("tex"), 1)
        GL13.glActiveTexture(GL13.GL_TEXTURE1)
        val texture1 = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D)
        GlStateManager._bindTexture(framebuffer.colorAttachment)

        GL20.glUniform2f(cutoutShader.getUniformLocation("resolution"), shapesBuffer.textureWidth.toFloat(), shapesBuffer.textureHeight.toFloat())

        GL11.glBegin(GL11.GL_QUADS)
        GL11.glVertex2f(0F, 0F)
        GL11.glVertex2f(shapesBuffer.textureWidth.toFloat(), 0F)
        GL11.glVertex2f(shapesBuffer.textureWidth.toFloat(), shapesBuffer.textureHeight.toFloat())
        GL11.glVertex2f(0F, shapesBuffer.textureHeight.toFloat())
        GL11.glEnd()

        GL20.glUseProgram(prevShader)

        GL13.glActiveTexture(GL13.GL_TEXTURE1)
        GlStateManager._bindTexture(texture1)

        GL13.glActiveTexture(GL13.GL_TEXTURE0)
        GlStateManager._bindTexture(texture0)

        GL13.glActiveTexture(activeTexture)

        shapesBuffer.clear(MinecraftClient.IS_SYSTEM_MAC)
        mc.framebuffer.beginWrite(MinecraftClient.IS_SYSTEM_MAC)

        if (!texture2D) GL11.glDisable(GL11.GL_TEXTURE_2D)
        if (cullFace) GL11.glEnable(GL11.GL_CULL_FACE)
        GL11.glPopMatrix()
    }

}

abstract class Blur(val name: String) {

    abstract fun render(targetBuffer: Framebuffer, strength: Int): Framebuffer
}
