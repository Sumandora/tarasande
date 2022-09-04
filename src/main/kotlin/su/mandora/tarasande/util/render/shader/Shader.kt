package su.mandora.tarasande.util.render.shader

import org.apache.commons.io.IOUtils
import org.lwjgl.opengl.GL20.*
import java.io.IOException
import java.util.*

class Shader(private val source: String, type: Int) {

    val id = glCreateShader(type)

    init {
        try {
            glShaderSource(id, String(IOUtils.toByteArray(Objects.requireNonNull(Shader::class.java.getResourceAsStream("$source")))))
        } catch (e: IOException) {
            e.printStackTrace()
        }

        glCompileShader(id)
        if (glGetShaderi(id, GL_COMPILE_STATUS) != GL_TRUE)
            error("$source " + glGetShaderInfoLog(id))

    }

    fun delete() {
        glDeleteShader(id)
        if (glGetShaderi(id, GL_DELETE_STATUS) != GL_TRUE)
            error("$source " + glGetShaderInfoLog(id))
    }

}