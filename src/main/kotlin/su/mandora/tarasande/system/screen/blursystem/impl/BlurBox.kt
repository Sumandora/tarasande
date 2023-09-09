package su.mandora.tarasande.system.screen.blursystem.impl

import net.minecraft.client.gl.Framebuffer
import net.minecraft.client.util.math.MatrixStack
import org.lwjgl.opengl.GL20
import su.mandora.tarasande.system.screen.blursystem.Blur
import su.mandora.tarasande.util.render.RenderUtil
import su.mandora.tarasande.util.render.framebuffer.SimpleFramebufferWrapped
import su.mandora.tarasande.util.render.shader.Program
import su.mandora.tarasande.util.render.shader.Shader

class BlurBox : Blur("Box") {
    private val box = Program(Shader("blur/box/box.frag", GL20.GL_FRAGMENT_SHADER), Shader("default.vert", GL20.GL_VERTEX_SHADER))

    private val blurredFramebuffer = SimpleFramebufferWrapped()
    private val alternativeFramebuffer = SimpleFramebufferWrapped()

    override fun render(matrices: MatrixStack, targetBuffer: Framebuffer, strength: Int): Framebuffer {
        sample(matrices, strength, targetBuffer, alternativeFramebuffer, Direction.HORIZONTAL)
        sample(matrices, strength, alternativeFramebuffer, blurredFramebuffer, Direction.VERTICAL)
        return blurredFramebuffer
    }

    private fun sample(matrices: MatrixStack, strength: Int, read: Framebuffer, write: Framebuffer, direction: Direction) {
        write.beginWrite(true)
        box.bindProgram()

        box["size"] = strength
        box["tex"] = read
        box["direction"] = direction
        box["resolution"] = floatArrayOf(write.textureWidth.toFloat(), write.textureHeight.toFloat())

        RenderUtil.quad(matrices)

        box.unbindProgram()
    }

    enum class Direction(var x: Int, var y: Int) {
        HORIZONTAL(1, 0), VERTICAL(0, 1)
    }

    operator fun Program.set(uniformName: String, value: Direction) = GL20.glUniform2i(get(uniformName), value.x, value.y)
}