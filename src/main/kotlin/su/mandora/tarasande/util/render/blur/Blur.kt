package su.mandora.tarasande.util.render.blur


import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gl.Framebuffer
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL20
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.event.EventRender2D
import su.mandora.tarasande.event.EventScreenRender
import su.mandora.tarasande.screen.menu.ScreenMenu
import su.mandora.tarasande.util.render.framebuffer.WrappedFramebuffer
import su.mandora.tarasande.util.render.shader.Shader

class Blur {
    private val upsample = Shader("blur/upsample", "default")
    private val downsample = Shader("blur/downsample", "default")

    private val shapesFramebuffer = WrappedFramebuffer()
    private val alternativeFramebuffer = WrappedFramebuffer()
    private val blurredFramebuffer = WrappedFramebuffer()

    private val cutoutShader = Shader("cutout", "default")

    var kawasePasses: ArrayList<Pair<Float, Float>>? = null

    private val strengthLevels = ArrayList<Pair<Int, Float>>()

    init {
        // https://github.com/jonaburg/picom/blob/a8445684fe18946604848efb73ace9457b29bf80/src/backend/backend_common.c#L372
        strengthLevels.add(Pair(1, 1.25f))
        strengthLevels.add(Pair(1, 2.25f))
        strengthLevels.add(Pair(2, 2.00f))
        strengthLevels.add(Pair(2, 3.00f))
        strengthLevels.add(Pair(2, 4.25f))
        strengthLevels.add(Pair(3, 2.50f))
        strengthLevels.add(Pair(3, 3.25f))
        strengthLevels.add(Pair(3, 4.25f))
        strengthLevels.add(Pair(3, 5.50f))
        strengthLevels.add(Pair(4, 3.25f))
        strengthLevels.add(Pair(4, 4.00f))
        strengthLevels.add(Pair(4, 5.00f))
        strengthLevels.add(Pair(4, 6.00f))
        strengthLevels.add(Pair(4, 7.25f))
        strengthLevels.add(Pair(4, 8.25f))
        strengthLevels.add(Pair(5, 4.50f))
        strengthLevels.add(Pair(5, 5.25f))
        strengthLevels.add(Pair(5, 6.25f))
        strengthLevels.add(Pair(5, 7.25f))
        strengthLevels.add(Pair(5, 8.50f))

        TarasandeMain.get().managerEvent?.add { event ->
            if (event is EventScreenRender) {
                if (MinecraftClient.getInstance().world == null || MinecraftClient.getInstance().currentScreen is ScreenMenu)
                    blurScene()
            } else if (event is EventRender2D) {
                blurScene()
            }
        }
    }

    fun bind(setViewport: Boolean) {
        shapesFramebuffer.beginWrite(setViewport)
    }

    fun blurScene(strength: Int? = null) {
        if (!RenderSystem.isOnRenderThread())
            return

        val texture2D = GL11.glIsEnabled(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        val cullFace = GL11.glIsEnabled(GL11.GL_CULL_FACE)
        GL11.glDisable(GL11.GL_CULL_FACE)

        var last: Framebuffer? = null

        var totalScale = 1.0f

        if (kawasePasses == null) {
            kawasePasses = calculateKawasePasses(TarasandeMain.get().clientValues?.blurStrength?.value?.toInt()!!)
        }

        for ((index, kawasePass) in (if (strength != null) calculateKawasePasses(strength) else kawasePasses!!).withIndex()) {
            var read: Framebuffer? = null
            var write: Framebuffer? = null

            when {
                index == 0 -> {
                    read = MinecraftClient.getInstance().framebuffer
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

        MinecraftClient.getInstance().framebuffer.beginWrite(MinecraftClient.IS_SYSTEM_MAC)

        val prevShader = cutoutShader.bindProgram()
        GL20.glUniform1i(cutoutShader.getUniformLocation("shapes"), 0)
        GL13.glActiveTexture(GL13.GL_TEXTURE0)
        val texture0 = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D)
        GlStateManager._bindTexture(shapesFramebuffer.colorAttachment)

        GL20.glUniform1i(cutoutShader.getUniformLocation("tex"), 1)
        GL13.glActiveTexture(GL13.GL_TEXTURE1)
        val texture1 = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D)
        GlStateManager._bindTexture(last?.colorAttachment!!)

        GL20.glUniform2f(cutoutShader.getUniformLocation("resolution"), blurredFramebuffer.textureWidth.toFloat(), blurredFramebuffer.textureHeight.toFloat())

        GL11.glBegin(GL11.GL_QUADS)
        GL11.glVertex2f(0f, 0f)
        GL11.glVertex2f(blurredFramebuffer.textureWidth.toFloat(), 0f)
        GL11.glVertex2f(blurredFramebuffer.textureWidth.toFloat(), blurredFramebuffer.textureHeight.toFloat())
        GL11.glVertex2f(0f, blurredFramebuffer.textureHeight.toFloat())
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
        val invertedHeight = if (scale == 1.0f) read.textureHeight.toFloat() else read.textureHeight - read.textureHeight * scale
        GL11.glVertex2f(0f, 0f)
        GL11.glVertex2f(read.textureWidth * scale, 0f)
        GL11.glVertex2f(read.textureWidth * scale, invertedHeight)
        GL11.glVertex2f(0f, invertedHeight)
        GL11.glEnd()
        GL20.glUseProgram(prevProgram)
        GL13.glActiveTexture(GL13.GL_TEXTURE0)
        GlStateManager._bindTexture(texture0)
        return write
    }

    private fun calculateKawasePasses(strength: Int): ArrayList<Pair<Float, Float>> {
        val passes = ArrayList<Pair<Float, Float>>()
        val pair = strengthLevels[strength - 1]
        for (i in 0 until pair.first * 2)
            passes.add(Pair(pair.second, 1.0f))
        for ((index, pass) in passes.withIndex()) {
            if (index < passes.size / 2)
                passes[index] = Pair(pass.first, 0.5f)
            else
                passes[index] = Pair(pass.first, 2.0f)
        }
        if (passes.size % 2 != 0) {
            val pass = passes[passes.size - 1]
            passes[passes.size - 1] = Pair(pass.first, 1.0f)
        }
        return passes
    }
}