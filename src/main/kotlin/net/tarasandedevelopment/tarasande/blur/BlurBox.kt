package net.tarasandedevelopment.tarasande.blur

import com.mojang.blaze3d.platform.GlStateManager
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gl.Framebuffer
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.blur.Blur
import net.tarasandedevelopment.tarasande.util.render.framebuffer.SimpleFramebufferWrapped
import net.tarasandedevelopment.tarasande.util.render.shader.Program
import net.tarasandedevelopment.tarasande.util.render.shader.Shader
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL20

class BlurBox : Blur("Box") {
    private val box = Program(Shader("blur/box/box.frag", GL20.GL_FRAGMENT_SHADER), Shader("default.vert", GL20.GL_VERTEX_SHADER))

    private val blurredFramebuffer = SimpleFramebufferWrapped()
    private val alternativeFramebuffer = SimpleFramebufferWrapped()

    override fun render(strength: Int?): Framebuffer {
        val strength = strength ?: TarasandeMain.get().clientValues.blurStrength.value.toInt()
        sample(strength, MinecraftClient.getInstance().framebuffer, alternativeFramebuffer, Direction.HORIZONTAL)
        sample(strength, alternativeFramebuffer, blurredFramebuffer, Direction.VERTICAL)
        return blurredFramebuffer
    }

    private fun sample(strength: Int, read: Framebuffer, write: Framebuffer, direction: Direction) {
        write.beginWrite(true)
        val prevProgram: Int = box.bindProgram()
        GL20.glUniform1i(box.getUniformLocation("size"), strength)
        GL20.glUniform1i(box.getUniformLocation("tex"), 0)
        GL13.glActiveTexture(GL13.GL_TEXTURE0)
        val texture0 = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D)
        GlStateManager._bindTexture(read.colorAttachment)
        GL20.glUniform2f(box.getUniformLocation("direction"), direction.x.toFloat(), direction.y.toFloat())
        GL20.glUniform2f(box.getUniformLocation("resolution"), write.textureWidth.toFloat(), write.textureHeight.toFloat())

        GL11.glBegin(GL11.GL_QUADS)
        GL11.glVertex2f(0f, 0f)
        GL11.glVertex2f(write.textureWidth.toFloat(), 0f)
        GL11.glVertex2f(write.textureWidth.toFloat(), write.textureHeight.toFloat())
        GL11.glVertex2f(0f, write.textureHeight.toFloat())
        GL11.glEnd()

        GL20.glUseProgram(prevProgram)
        GL13.glActiveTexture(GL13.GL_TEXTURE0)
        GlStateManager._bindTexture(texture0)
    }

    enum class Direction(var x: Int, var y: Int) {
        HORIZONTAL(1, 0), VERTICAL(0, 1);
    }
}