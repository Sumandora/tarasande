package su.mandora.obfuscator

import org.objectweb.asm.ClassReader
import org.objectweb.asm.tree.ClassNode
import java.io.File
import java.io.FileInputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class ObfuscatorMain(private val input: File, private val output: File) {

    fun obfuscate() {
        val nodes = readNodes(input)
        Linker.link(nodes.filterIsInstance<ClassFile>().map { it.classNode })
        println("Initialized")
    }

    private fun readNodes(input: File): ArrayList<JarFileEntry> {
        val zis = ZipInputStream(FileInputStream(input))
        val nodes = ArrayList<JarFileEntry>()

        var zipEntry: ZipEntry?
        while(zis.nextEntry.also { zipEntry = it } != null) {
            val bytes = zis.readAllBytes()
            if(zipEntry!!.name.endsWith(".class"))
                nodes.add(ClassNode().apply { ClassReader(bytes).accept(this, ClassReader.EXPAND_FRAMES) }.let { ClassFile(it) } )
            else
                nodes.add(ResourceFile(zipEntry!!.name, bytes))
            zis.closeEntry()
        }

        zis.close()
        return nodes
    }
}
