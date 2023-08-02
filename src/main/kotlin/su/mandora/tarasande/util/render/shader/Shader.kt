package su.mandora.tarasande.util.render.shader

import com.mojang.blaze3d.platform.GlConst
import com.mojang.blaze3d.platform.GlStateManager
import org.lwjgl.opengl.GL20

class Shader(private val source: String, type: Int): AutoCloseable {

    val id = GlStateManager.glCreateShader(type)

    init {
        GL20.glShaderSource(id, (Shader::class.java.getResourceAsStream(source) ?: error("Can't acquire shader source")).readAllBytes().decodeToString())

        GlStateManager.glCompileShader(id)
        if (GlStateManager.glGetShaderi(id, GlConst.GL_COMPILE_STATUS) != GlConst.GL_TRUE)
            error(source + " " + GlStateManager.glGetShaderInfoLog(id, Int.MAX_VALUE))

    }

    override fun close() {
        GlStateManager.glDeleteShader(id)
        if (GlStateManager.glGetShaderi(id, GL20.GL_DELETE_STATUS) != GlConst.GL_TRUE)
            error(source + " " + GlStateManager.glGetShaderInfoLog(id, Int.MAX_VALUE))
    }

}