package su.mandora.tarasande.system.screen.blursystem.impl

import net.minecraft.client.gl.Framebuffer
import net.minecraft.client.gui.DrawContext
import org.lwjgl.opengl.GL20
import su.mandora.tarasande.system.screen.blursystem.Blur
import su.mandora.tarasande.util.render.RenderUtil
import su.mandora.tarasande.util.render.framebuffer.SimpleFramebufferWrapped
import su.mandora.tarasande.util.render.shader.Program
import su.mandora.tarasande.util.render.shader.Shader

class BlurGaussian : Blur("Gaussian") {
    private val gaussian = Program(Shader("blur/gaussian/gaussian.frag", GL20.GL_FRAGMENT_SHADER), Shader("default.vert", GL20.GL_VERTEX_SHADER))

    private val blurredFramebuffer = SimpleFramebufferWrapped()
    private val alternativeFramebuffer = SimpleFramebufferWrapped()

    override fun render(context: DrawContext, targetBuffer: Framebuffer, strength: Int): Framebuffer {
        sample(context, strength, targetBuffer, alternativeFramebuffer, Direction.HORIZONTAL)
        sample(context, strength, alternativeFramebuffer, blurredFramebuffer, Direction.VERTICAL)
        return blurredFramebuffer
    }

    private fun sample(context: DrawContext, strength: Int, read: Framebuffer, write: Framebuffer, direction: Direction) {
        write.beginWrite(true)
        gaussian.bindProgram()

        gaussian["sigma"] = strength.toFloat()
        gaussian["tex"] = read
        gaussian["direction"] = direction
        gaussian["resolution"] = floatArrayOf(write.textureWidth.toFloat(), write.textureHeight.toFloat())

        RenderUtil.quad(context)

        gaussian.unbindProgram()
    }

    enum class Direction(var x: Int, var y: Int) {
        HORIZONTAL(1, 0), VERTICAL(0, 1);
    }

    operator fun Program.set(uniformName: String, value: Direction) = GL20.glUniform2i(get(uniformName), value.x, value.y)
}