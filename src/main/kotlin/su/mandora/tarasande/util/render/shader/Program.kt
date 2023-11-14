package su.mandora.tarasande.util.render.shader

import com.mojang.blaze3d.platform.GlConst
import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gl.Framebuffer
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import su.mandora.tarasande.logger
import java.util.logging.Level

class Program(vararg shaders: Shader): AutoCloseable {

    private var programId = 0
    private val uniformLocations = HashMap<String, Int>()

    private var prevProgramId = 0
    private var prevTextures = ArrayList<Int>()


    init {
        programId = GlStateManager.glCreateProgram()
        for (shader in shaders)
            GlStateManager.glAttachShader(programId, shader.id)
        GlStateManager.glLinkProgram(programId)
        if (GlStateManager.glGetProgrami(programId, GlConst.GL_LINK_STATUS) != GlConst.GL_TRUE)
            error(GlStateManager.glGetProgramInfoLog(programId, Int.MAX_VALUE))

        // Don't error out if validation fails, the program might still be valid
        // For more information check: https://github.com/Sumandora/tarasande/issues/14
        GL20.glValidateProgram(programId)
        if (GlStateManager.glGetProgrami(programId, GL20.GL_VALIDATE_STATUS) != GlConst.GL_TRUE)
            logger.log(Level.WARNING, "Shader validation unsuccessful", IllegalStateException(GlStateManager.glGetProgramInfoLog(programId, Int.MAX_VALUE)))

        for (shader in shaders)
            shader.close()
    }

    fun bindProgram() {
        prevProgramId = GlStateManager._getInteger(GL20.GL_CURRENT_PROGRAM)
        GlStateManager._glUseProgram(programId)
    }

    fun unbindProgram() {
        for((index, previous) in prevTextures.withIndex()) {
            RenderSystem.activeTexture(GlConst.GL_TEXTURE0 + index)
            RenderSystem.bindTexture(previous)
        }
        prevTextures.clear()
        RenderSystem.activeTexture(GlConst.GL_TEXTURE0)
        GlStateManager._glUseProgram(prevProgramId)
    }

    operator fun get(uniformName: String) = uniformLocations.computeIfAbsent(uniformName) {
        GlStateManager._glGetUniformLocation(programId, uniformName)
    }

    operator fun set(uniformName: String, value: Int) = RenderSystem.glUniform1i(this[uniformName], value)

    operator fun set(uniformName: String, value: Framebuffer) {
        this[uniformName] = prevTextures.size
        RenderSystem.activeTexture(GlConst.GL_TEXTURE0 + prevTextures.size)
        prevTextures.add(GlStateManager._getInteger(GL11.GL_TEXTURE_BINDING_2D))
        RenderSystem.bindTexture(value.colorAttachment)
    }

    operator fun set(uniformName: String, value: Float) = GL20.glUniform1f(get(uniformName), value)

    operator fun set(uniformName: String, value: FloatArray) {
        when (value.size) {
            2 -> GL20.glUniform2f(get(uniformName), value[0], value[1])
            3 -> GL20.glUniform3f(get(uniformName), value[0], value[1], value[2])
            4 -> GL20.glUniform4f(get(uniformName), value[0], value[1], value[2], value[3])
            else -> error("Invalid array size")
        }
    }

    operator fun set(uniformName: String, value: Boolean) {
        GL20.glUniform1i(get(uniformName), if(value) 1 else 0)
    }

    override fun close() {
        GlStateManager.glDeleteProgram(programId)
    }
}
