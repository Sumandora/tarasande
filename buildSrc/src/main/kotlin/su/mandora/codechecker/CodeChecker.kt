package su.mandora.codechecker

import org.gradle.api.tasks.SourceSet
import org.objectweb.asm.ClassReader
import org.objectweb.asm.tree.ClassNode
import su.mandora.codechecker.check.CheckManager
import java.io.File

class CodeChecker(private val sources: SourceSet) {

    private val checkManager = CheckManager()

    private val ignoreList = ArrayList<String>()

    fun check() {
        val sources = ArrayList<File>()
        this.sources.allSource.srcDirs.forEach {
            it.walk().forEach { file ->
                if(!file.isDirectory && ignoreList.none { file.path.contains(it) })
                    sources.add(file)
            }
        }

        checkManager.checkSource(sources)

        val classNodes = ArrayList<ClassNode>()
        this.sources.output.classesDirs.forEach {
            it.walk().forEach { file ->
                if(!file.isDirectory && file.extension == "class" && ignoreList.none { file.path.contains(it) }) {
                    val node = ClassNode()
                    val reader = ClassReader(file.readBytes())
                    reader.accept(node, ClassReader.EXPAND_FRAMES)
                    classNodes.add(node)
                }
            }
        }

        checkManager.checkBytecode(classNodes)
    }

    fun ignore(string: String) {
        ignoreList.add(string)
    }

}