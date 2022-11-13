package net.tarasandedevelopment.tarasande.util.render.shader

import org.lwjgl.opengl.GL20.*
import java.io.IOException

class Shader(private val source: String, type: Int) {

    val id = glCreateShader(type)

    init {
        try {
            glShaderSource(id, (Shader::class.java.getResourceAsStream(source) ?: error("Can't acquire shader source")).readAllBytes().decodeToString())
        } catch (e: IOException) {
            e.printStackTrace()
        }

        glCompileShader(id)
        if (glGetShaderi(id, GL_COMPILE_STATUS) != GL_TRUE)
            error(source + " " + glGetShaderInfoLog(id))

    }

    fun delete() {
        glDeleteShader(id)
        if (glGetShaderi(id, GL_DELETE_STATUS) != GL_TRUE)
            error(source + " " + glGetShaderInfoLog(id))
    }

}