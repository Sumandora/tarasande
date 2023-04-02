package su.mandora.tarasande.system.screen.blursystem.impl

import com.mojang.blaze3d.platform.GlStateManager
import net.minecraft.client.gl.Framebuffer
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL20
import su.mandora.tarasande.system.screen.blursystem.Blur
import su.mandora.tarasande.util.render.framebuffer.SimpleFramebufferWrapped
import su.mandora.tarasande.util.render.shader.Program
import su.mandora.tarasande.util.render.shader.Shader

class BlurKawase : Blur("Kawase") {
    private val upsample = Program(Shader("blur/kawase/upsample.frag", GL20.GL_FRAGMENT_SHADER), Shader("default.vert", GL20.GL_VERTEX_SHADER))
    private val downsample = Program(Shader("blur/kawase/downsample.frag", GL20.GL_FRAGMENT_SHADER), Shader("default.vert", GL20.GL_VERTEX_SHADER))

    private val blurredFramebuffer = SimpleFramebufferWrapped()
    private val alternativeFramebuffer = SimpleFramebufferWrapped()

    private var kawasePasses: HashMap<Int, Array<Pair<Float, Float>>> = HashMap()


    init {
        // https://github.com/jonaburg/picom/blob/a8445684fe18946604848efb73ace9457b29bf80/src/backend/backend_common.c#L372
        val strengthLevels = ArrayList<Pair<Int, Float>>()
        strengthLevels.add(Pair(1, 1.25F))
        strengthLevels.add(Pair(1, 2.25F))
        strengthLevels.add(Pair(2, 2.00F))
        strengthLevels.add(Pair(2, 3.00F))
        strengthLevels.add(Pair(2, 4.25F))
        strengthLevels.add(Pair(3, 2.50F))
        strengthLevels.add(Pair(3, 3.25F))
        strengthLevels.add(Pair(3, 4.25F))
        strengthLevels.add(Pair(3, 5.50F))
        strengthLevels.add(Pair(4, 3.25F))
        strengthLevels.add(Pair(4, 4.00F))
        strengthLevels.add(Pair(4, 5.00F))
        strengthLevels.add(Pair(4, 6.00F))
        strengthLevels.add(Pair(4, 7.25F))
        strengthLevels.add(Pair(4, 8.25F))
        strengthLevels.add(Pair(5, 4.50F))
        strengthLevels.add(Pair(5, 5.25F))
        strengthLevels.add(Pair(5, 6.25F))
        strengthLevels.add(Pair(5, 7.25F))
        strengthLevels.add(Pair(5, 8.50F))

        for ((index, pair) in strengthLevels.withIndex()) {
            val passes = Array(pair.first * 2) { Pair(pair.second, 1.0F) }
            @Suppress("NAME_SHADOWING")
            for ((index, pass) in passes.withIndex()) {
                passes[index] = Pair(pass.first,
                    if (index < passes.size / 2)
                        0.5F
                    else
                        2.0F
                )
            }
            if (passes.size % 2 != 0) {
                val pass = passes.size - 1
                passes[pass] = passes[pass].copy(second = 1.0F)
            }
            kawasePasses[index] = passes
        }
    }

    override fun render(targetBuffer: Framebuffer, strength: Int): Framebuffer {
        lateinit var last: Framebuffer

        var totalScale = 1.0F

        for ((index, kawasePass) in kawasePasses[strength - 1]!!.withIndex()) {
            var read: Framebuffer? = null
            var write: Framebuffer? = null

            when {
                index == 0 -> {
                    read = targetBuffer
                    write = alternativeFramebuffer
                }

                index % 2 != 0 -> {
                    read = alternativeFramebuffer
                    write = blurredFramebuffer
                }

                index % 2 == 0 -> {
                    read = blurredFramebuffer
                    write = alternativeFramebuffer
                }
            }

            totalScale *= kawasePass.second

            last = sample(read!!, write!!, kawasePass.first, totalScale, kawasePass.second)
        }

        return last
    }

    private fun sample(read: Framebuffer, write: Framebuffer, offset: Float, scale: Float, deltaScale: Float): Framebuffer {
        val shader = if (deltaScale > 1.0) upsample else downsample
        write.beginWrite(true)
        val prevProgram = shader.bindProgram()
        GL20.glUniform1f(shader.getUniformLocation("offset"), offset)
        GL20.glUniform1i(shader.getUniformLocation("tex"), 0)
        GL13.glActiveTexture(GL13.GL_TEXTURE0)
        val texture0 = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D)
        GlStateManager._bindTexture(read.colorAttachment)
        GL20.glUniform2f(shader.getUniformLocation("resolution"), read.textureWidth * deltaScale, read.textureHeight * deltaScale)
        GL11.glBegin(GL11.GL_QUADS)
        val invertedHeight = if (scale == 1.0F) read.textureHeight.toFloat() else read.textureHeight - read.textureHeight * scale
        GL11.glVertex2f(0F, 0F)
        GL11.glVertex2f(read.textureWidth * scale, 0F)
        GL11.glVertex2f(read.textureWidth * scale, invertedHeight)
        GL11.glVertex2f(0F, invertedHeight)
        GL11.glEnd()
        GL20.glUseProgram(prevProgram)
        GL13.glActiveTexture(GL13.GL_TEXTURE0)
        GlStateManager._bindTexture(texture0)
        return write
    }
}