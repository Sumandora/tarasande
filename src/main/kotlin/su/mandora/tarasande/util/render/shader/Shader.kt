package su.mandora.tarasande.util.render.shader

import org.apache.commons.io.IOUtils
import org.lwjgl.opengl.GL20.*
import java.io.IOException
import java.util.*

class Shader(fragment: String, vertex: String) {

	private var programId = 0
	private val uniformLocations = HashMap<String, Int>()

	init {
		val fragmentId: Int = glCreateShader(GL_FRAGMENT_SHADER)
		val vertexId: Int = glCreateShader(GL_VERTEX_SHADER)
		try {
			glShaderSource(fragmentId, String(IOUtils.toByteArray(Objects.requireNonNull(Shader::class.java.getResourceAsStream("$fragment.frag")))))
			glShaderSource(vertexId, String(IOUtils.toByteArray(Objects.requireNonNull(Shader::class.java.getResourceAsStream("$vertex.vert")))))
		} catch (e: IOException) {
			e.printStackTrace()
		}
		glCompileShader(fragmentId)
		if (glGetShaderi(fragmentId, GL_COMPILE_STATUS) != GL_TRUE)
			error("$fragment " + glGetShaderInfoLog(fragmentId))
		glCompileShader(vertexId)
		if (glGetShaderi(vertexId, GL_COMPILE_STATUS) != GL_TRUE)
			error("$vertex " + glGetShaderInfoLog(vertexId))
		programId = glCreateProgram()
		glAttachShader(programId, fragmentId)
		glAttachShader(programId, vertexId)
		glLinkProgram(programId)
		if (glGetProgrami(programId, GL_LINK_STATUS) != GL_TRUE)
			error("$fragment $vertex " + glGetProgramInfoLog(programId))
		glValidateProgram(programId)
		if (glGetProgrami(programId, GL_VALIDATE_STATUS) != GL_TRUE)
			error("$fragment $vertex " + glGetProgramInfoLog(programId))
		glDeleteShader(fragmentId)
		if (glGetShaderi(fragmentId, GL_DELETE_STATUS) != GL_TRUE)
			error("$fragment $vertex " + glGetShaderInfoLog(fragmentId))
		glDeleteShader(vertexId)
		if (glGetShaderi(vertexId, GL_DELETE_STATUS) != GL_TRUE)
			error("$fragment $vertex " + glGetShaderInfoLog(vertexId))
	}

	fun bindProgram(): Int {
		val prevProgramId: Int = glGetInteger(GL_CURRENT_PROGRAM)
		glUseProgram(programId)
		return prevProgramId
	}

	fun getUniformLocation(uniformName: String): Int {
		return uniformLocations.computeIfAbsent(uniformName) {
			glGetUniformLocation(programId, uniformName)
		}
	}

}