package net.tarasandedevelopment.tarasande.systems.screen.blursystem

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gl.Framebuffer
import net.minecraft.client.gui.screen.DownloadingTerrainScreen
import net.minecraft.client.realms.gui.screen.RealmsNotificationsScreen
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.Manager
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.ValueMode
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.events.impl.EventRender2D
import net.tarasandedevelopment.events.impl.EventScreenRender
import net.tarasandedevelopment.tarasande.systems.screen.blursystem.impl.BlurBox
import net.tarasandedevelopment.tarasande.systems.screen.blursystem.impl.BlurGaussian
import net.tarasandedevelopment.tarasande.systems.screen.blursystem.impl.BlurKawase
import net.tarasandedevelopment.tarasande.systems.screen.panelsystem.screen.ScreenCheatMenu
import net.tarasandedevelopment.tarasande.util.render.framebuffer.SimpleFramebufferWrapped
import net.tarasandedevelopment.tarasande.util.render.shader.Program
import net.tarasandedevelopment.tarasande.util.render.shader.Shader
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL20

class ManagerBlur : Manager<Blur>() {

    val mode: ValueMode
    val strength = ValueNumber(this, "Blur strength", 1.0, 1.0, 20.0, 1.0)

    private val shapesFramebuffer = SimpleFramebufferWrapped()
    private val cutoutShader = Program(Shader("cutout.frag", GL20.GL_FRAGMENT_SHADER), Shader("default.vert", GL20.GL_VERTEX_SHADER))

    init {
        add(
            BlurBox(),
            BlurGaussian(),
            BlurKawase()
        )

        TarasandeMain.get().eventSystem.also {
            it.add(EventScreenRender::class.java) {
                if ((MinecraftClient.getInstance().world == null || MinecraftClient.getInstance().currentScreen is ScreenCheatMenu || MinecraftClient.getInstance().currentScreen is DownloadingTerrainScreen) && it.screen !is RealmsNotificationsScreen)
                    blurScene()
            }
            it.add(EventRender2D::class.java) {
                blurScene()
            }
        }

        mode = ValueMode(this, "Blur mode", false, *list.map { it.name }.toTypedArray())
    }

    fun selected(): Blur = list[mode.let { it.settings.indexOf(it.selected[0]) }]

    fun bind(setViewport: Boolean) {
        shapesFramebuffer.beginWrite(setViewport)
    }

    internal fun blurScene(strength: Int? = null) {
        if (!RenderSystem.isOnRenderThread()) return

        GL11.glPushMatrix()
        val texture2D = GL11.glIsEnabled(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        val cullFace = GL11.glIsEnabled(GL11.GL_CULL_FACE)
        GL11.glDisable(GL11.GL_CULL_FACE)

        val framebuffer = selected().render(strength ?: this.strength.value.toInt())

        MinecraftClient.getInstance().framebuffer.beginWrite(MinecraftClient.IS_SYSTEM_MAC)

        val prevShader = cutoutShader.bindProgram()
        GL20.glUniform1i(cutoutShader.getUniformLocation("shapes"), 0)
        GL13.glActiveTexture(GL13.GL_TEXTURE0)
        val texture0 = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D)
        GlStateManager._bindTexture(shapesFramebuffer.colorAttachment)

        GL20.glUniform1i(cutoutShader.getUniformLocation("tex"), 1)
        GL13.glActiveTexture(GL13.GL_TEXTURE1)
        val texture1 = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D)
        GlStateManager._bindTexture(framebuffer.colorAttachment)

        GL20.glUniform2f(cutoutShader.getUniformLocation("resolution"), shapesFramebuffer.textureWidth.toFloat(), shapesFramebuffer.textureHeight.toFloat())

        GL11.glBegin(GL11.GL_QUADS)
        GL11.glVertex2f(0f, 0f)
        GL11.glVertex2f(shapesFramebuffer.textureWidth.toFloat(), 0f)
        GL11.glVertex2f(shapesFramebuffer.textureWidth.toFloat(), shapesFramebuffer.textureHeight.toFloat())
        GL11.glVertex2f(0f, shapesFramebuffer.textureHeight.toFloat())
        GL11.glEnd()

        GL20.glUseProgram(prevShader)

        GL13.glActiveTexture(GL13.GL_TEXTURE1)
        GlStateManager._bindTexture(texture1)

        GL13.glActiveTexture(GL13.GL_TEXTURE0)
        GlStateManager._bindTexture(texture0)

        shapesFramebuffer.clear(MinecraftClient.IS_SYSTEM_MAC)
        MinecraftClient.getInstance().framebuffer.beginWrite(MinecraftClient.IS_SYSTEM_MAC)

        if (!texture2D) GL11.glDisable(GL11.GL_TEXTURE_2D)
        if (cullFace) GL11.glEnable(GL11.GL_CULL_FACE)
        GL11.glPopMatrix()
    }

}

abstract class Blur(val name: String) {
    abstract fun render(strength: Int): Framebuffer
}