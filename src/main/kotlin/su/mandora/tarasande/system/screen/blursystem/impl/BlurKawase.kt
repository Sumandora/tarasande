package su.mandora.tarasande.system.screen.blursystem.impl

import net.minecraft.client.gl.Framebuffer
import net.minecraft.client.util.math.MatrixStack
import org.lwjgl.opengl.GL20
import su.mandora.tarasande.system.screen.blursystem.Blur
import su.mandora.tarasande.util.render.RenderUtil
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
        val strengthLevels = arrayOf(
            1 to 1.25F,
            1 to 2.25F,
            2 to 2.00F,
            2 to 3.00F,
            2 to 4.25F,
            3 to 2.50F,
            3 to 3.25F,
            3 to 4.25F,
            3 to 5.50F,
            4 to 3.25F,
            4 to 4.00F,
            4 to 5.00F,
            4 to 6.00F,
            4 to 7.25F,
            4 to 8.25F,
            5 to 4.50F,
            5 to 5.25F,
            5 to 6.25F,
            5 to 7.25F,
            5 to 8.50F
        )

        for ((index, pair) in strengthLevels.withIndex()) {
            val passes = Array(pair.first * 2) { Pair(pair.second, 1F) }
            @Suppress("NAME_SHADOWING")
            for ((index, pass) in passes.withIndex()) {
                passes[index] = Pair(pass.first,
                    if (index < passes.size / 2)
                        0.5F
                    else
                        2F
                )
            }
            if (passes.size % 2 != 0) {
                val pass = passes.size - 1
                passes[pass] = passes[pass].copy(second = 1F)
            }
            kawasePasses[index] = passes
        }
    }

    override fun render(matrices: MatrixStack, targetBuffer: Framebuffer, strength: Int): Framebuffer {
        lateinit var last: Framebuffer

        var totalScale = 1F

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

            last = sample(matrices, read!!, write!!, kawasePass.first, totalScale, kawasePass.second)
        }

        return last
    }

    private fun sample(matrices: MatrixStack, read: Framebuffer, write: Framebuffer, offset: Float, scale: Float, deltaScale: Float): Framebuffer {
        val shader = if (deltaScale > 1.0) upsample else downsample
        write.beginWrite(true)
        shader.bindProgram()

        shader["offset"] = offset
        shader["tex"] = read
        shader["resolution"] = floatArrayOf(read.textureWidth * deltaScale, read.textureHeight * deltaScale)

        val invertedHeight = if (scale == 1F) read.textureHeight.toFloat() else read.textureHeight - read.textureHeight * scale
        RenderUtil.quad(matrices, read.textureWidth * scale, invertedHeight)

        shader.unbindProgram()
        return write
    }
}