package su.mandora.obfuscator

import org.objectweb.asm.tree.ClassNode

open class JarFileEntry {
}

class ClassFile(val classNode: ClassNode) : JarFileEntry() {

}

class ResourceFile(val name: String, val bytes: ByteArray) : JarFileEntry() {

}