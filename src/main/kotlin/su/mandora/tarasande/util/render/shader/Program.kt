package su.mandora.tarasande.util.render.shader

import org.lwjgl.opengl.GL20

class Program(vararg shaders: Shader) {

    private var programId = 0
    private val uniformLocations = HashMap<String, Int>()

    init {
        programId = GL20.glCreateProgram()
        for(shader in shaders)
            GL20.glAttachShader(programId, shader.id)
        GL20.glLinkProgram(programId)
        if (GL20.glGetProgrami(programId, GL20.GL_LINK_STATUS) != GL20.GL_TRUE)
            error(GL20.glGetProgramInfoLog(programId))
        GL20.glValidateProgram(programId)
        if (GL20.glGetProgrami(programId, GL20.GL_VALIDATE_STATUS) != GL20.GL_TRUE)
            error(GL20.glGetProgramInfoLog(programId))
        for(shader in shaders)
            shader.delete()
    }

    fun bindProgram(): Int {
        val prevProgramId: Int = GL20.glGetInteger(GL20.GL_CURRENT_PROGRAM)
        GL20.glUseProgram(programId)
        return prevProgramId
    }

    fun getUniformLocation(uniformName: String): Int {
        return uniformLocations.computeIfAbsent(uniformName) {
            GL20.glGetUniformLocation(programId, uniformName)
        }
    }
}