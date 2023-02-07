package su.mandora.codechecker.check

import org.objectweb.asm.tree.ClassNode
import su.mandora.codechecker.check.impl.bytecode.CheckBytecodeAccessWidenerUsage
import su.mandora.codechecker.check.impl.bytecode.CheckBytecodeNamingConvention
import su.mandora.codechecker.check.impl.bytecode.CheckBytecodeNoMixinOperation
import su.mandora.codechecker.check.impl.source.CheckSourceLowercaseNumberSuffix
import su.mandora.codechecker.check.impl.source.CheckSourcePluralPackage
import su.mandora.codechecker.check.impl.source.CheckSourceUnnecessaryNumberSuffix
import su.mandora.codechecker.check.impl.source.CheckSourceUnregisteredMixin
import java.io.File

class CheckManager {

    private lateinit var sources: ArrayList<File>
    private lateinit var nodes: ArrayList<ClassNode>

    private val checks: Array<Check> = arrayOf(
        CheckSourceLowercaseNumberSuffix(),
        CheckSourceUnnecessaryNumberSuffix(),
        CheckSourcePluralPackage(),
        CheckSourceUnregisteredMixin(),

        CheckBytecodeNamingConvention(),
        CheckBytecodeAccessWidenerUsage(),
        //CheckBytecodeAccessModifier(), // broken
        CheckBytecodeNoMixinOperation()
    )

    fun provideSources(list: ArrayList<File>) {
        this.sources = list
    }

    fun provideBytecode(list: ArrayList<ClassNode>) {
        this.nodes = list
    }

    fun runChecks() {
        for(check in checks) {
            when (check) {
                is CheckSource -> {
                    check.setSources(sources.associateWith { it.readBytes().decodeToString() })
                }

                is CheckBytecode -> {
                    check.setNodes(nodes)
                }
            }
            check.run()
        }
    }
}

abstract class Check(private val name: String) {
    protected fun getFile(sourceTree: String, path: String): File {
        return File("src/main/$sourceTree/$path")
    }

    abstract fun run()

    protected fun violation(output: String) {
        println("[$name] $output")
    }
}

abstract class CheckBytecode(private val name: String) : Check(name) {
    private lateinit var nodes: ArrayList<ClassNode>

    fun setNodes(nodes: ArrayList<ClassNode>) {
        this.nodes = nodes
    }

    protected fun allNodes() = nodes

    protected fun violation(classNode: ClassNode, output: String) {
        println("[$name | " + classNode.name + "] $output")
    }
}

abstract class CheckSource(private val name: String) : Check(name) {
    private lateinit var map: Map<File, String>

    fun setSources(map: Map<File, String>) {
        this.map = map
    }

    protected fun allSources() = map.keys
    protected fun read(file: File) = map[file]!!

    protected fun violation(file: File, index: Int, output: String) {
        val content = file.readBytes().decodeToString()

        var qoutes = 0
        var comment = false

        var lineBeginIndex = index
        while(lineBeginIndex > 0 && content[lineBeginIndex - 1] != "\n"[0]) {
            if(content[lineBeginIndex - 1] == '"')
                qoutes++
            if(content[lineBeginIndex - 1] == '/') {
                if(comment)
                    return
                comment = true
            }
            lineBeginIndex--
        }

        if(qoutes.mod(2) != 0)
            return

        var lineEndIndex = index
        while(content[lineEndIndex] != "\n"[0])
            lineEndIndex++

        println(file.path + ": $name")
        println(content.substring(lineBeginIndex, lineEndIndex))
        var spaces = ""
        repeat(index - lineBeginIndex) {
            spaces += " "
        }
        println("$spaces^$output")
    }
}