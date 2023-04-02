package su.mandora.tarasande.system.screen.blursystem.impl

import com.mojang.blaze3d.platform.GlStateManager
import net.minecraft.client.gl.Framebuffer
import su.mandora.tarasande.system.screen.blursystem.Blur
import su.mandora.tarasande.util.render.framebuffer.SimpleFramebufferWrapped
import su.mandora.tarasande.util.render.shader.Program
import su.mandora.tarasande.util.render.shader.Shader
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL20

class BlurGaussian : Blur("Gaussian") {
    private val gaussian = Program(Shader("blur/gaussian/gaussian.frag", GL20.GL_FRAGMENT_SHADER), Shader("default.vert", GL20.GL_VERTEX_SHADER))

    private val blurredFramebuffer = SimpleFramebufferWrapped()
    private val alternativeFramebuffer = SimpleFramebufferWrapped()

    override fun render(targetBuffer: Framebuffer, strength: Int): Framebuffer {
        sample(strength, targetBuffer, alternativeFramebuffer, Direction.HORIZONTAL)
        sample(strength, alternativeFramebuffer, blurredFramebuffer, Direction.VERTICAL)
        return blurredFramebuffer
    }

    private fun sample(strength: Int, read: Framebuffer, write: Framebuffer, direction: Direction) {
        write.beginWrite(true)
        val prevProgram: Int = gaussian.bindProgram()
        GL20.glUniform1f(gaussian.getUniformLocation("sigma"), strength.toFloat())
        GL20.glUniform1i(gaussian.getUniformLocation("tex"), 0)
        GL13.glActiveTexture(GL13.GL_TEXTURE0)
        val texture0 = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D)
        GlStateManager._bindTexture(read.colorAttachment)
        GL20.glUniform2f(gaussian.getUniformLocation("direction"), direction.x.toFloat(), direction.y.toFloat())
        GL20.glUniform2f(gaussian.getUniformLocation("resolution"), write.textureWidth.toFloat(), write.textureHeight.toFloat())
        GL11.glBegin(GL11.GL_QUADS)
        GL11.glVertex2f(0F, 0F)
        GL11.glVertex2f(write.textureWidth.toFloat(), 0F)
        GL11.glVertex2f(write.textureWidth.toFloat(), write.textureHeight.toFloat())
        GL11.glVertex2f(0F, write.textureHeight.toFloat())
        GL11.glEnd()
        GL20.glUseProgram(prevProgram)
        GL13.glActiveTexture(GL13.GL_TEXTURE0)
        GlStateManager._bindTexture(texture0)
    }

    enum class Direction(var x: Int, var y: Int) {
        HORIZONTAL(1, 0), VERTICAL(0, 1);
    }
}