package su.mandora.codechecker.check

import org.objectweb.asm.tree.ClassNode
import su.mandora.codechecker.check.impl.bytecode.CheckNamingConvention
import su.mandora.codechecker.check.impl.source.CheckLowercaseNumberSuffix
import su.mandora.codechecker.check.impl.source.CheckPluralPackage
import su.mandora.codechecker.check.impl.source.CheckUnnecessaryNumberSuffix
import su.mandora.codechecker.check.impl.source.CheckUnregisteredMixin
import java.io.File

class CheckManager {

    private val checks: Array<Check> = arrayOf(
        CheckLowercaseNumberSuffix(),
        CheckUnnecessaryNumberSuffix(),
        CheckPluralPackage(),
        CheckUnregisteredMixin(),

        CheckNamingConvention()
    )

    fun checkSource(list: ArrayList<File>) {
        for(check in checks) {
            if(check is CheckSource) {
                check.setSources(list.associateWith { it.readBytes().decodeToString() })
                check.run()
            }
        }
    }

    fun checkBytecode(list: ArrayList<ClassNode>) {
        for(check in checks) {
            if(check is CheckBytecode) {
                check.setNodes(list)
                check.run()
            }
        }
    }
}

abstract class Check(private val name: String) {
    protected fun getFile(sourceTree: String, path: String): File {
        return File("src/main/$sourceTree/$path")
    }

    abstract fun run()
}

abstract class CheckBytecode(private val name: String) : Check(name) {
    private lateinit var nodes: ArrayList<ClassNode>

    fun setNodes(nodes: ArrayList<ClassNode>) {
        this.nodes = nodes
    }

    protected fun allNodes() = nodes

    protected open fun violation(classNode: ClassNode, output: String) {
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
        while(lineBeginIndex > 0 && content[lineBeginIndex - 1] != '\n') {
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
        while(content[lineEndIndex] != '\n')
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