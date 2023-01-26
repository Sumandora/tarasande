package su.mandora.obfuscator

import org.objectweb.asm.tree.*
import java.util.*

// This class extends ASM Method- and FieldInsnNodes with the capability to jump between methods without a heavy lookup method

private val methods = WeakHashMap<MethodInsnNode, MethodNode>()
private val fields = WeakHashMap<FieldInsnNode, FieldNode>()

// Null will indicate a method which is out of bounds

fun MethodInsnNode.getMethod(): MethodNode? = methods[this]
fun FieldInsnNode.getField(): FieldNode? = fields[this]

object Linker {
    fun link(nodes: List<ClassNode>) {
        nodes.forEach {
            it.methods.forEach {
                it.instructions.forEach { insn ->
                    when(insn) {
                        is FieldInsnNode -> {
                            println("F: " + insn.owner + " " + insn.name + " " + insn.desc)
                            var currentClass = insn.owner
                            while(currentClass != "java/lang/Object") {
                                @Suppress("LABEL_NAME_CLASH")
                                val clazz = nodes.firstOrNull { it.name == currentClass } ?: return@forEach
                                val field = clazz.fields.firstOrNull { it.name == insn.name && it.desc == insn.desc }
                                if(field == null) {
                                    println("Checking superclass: " + clazz.superName)
                                    currentClass = clazz.superName
                                } else {
                                    fields[insn] = field
                                    @Suppress("LABEL_NAME_CLASH")
                                    return@forEach
                                }
                            }
                        }
                        is MethodInsnNode -> {
                            println("M: " + insn.owner + " " + insn.name + " " + insn.desc)
                            var currentClass = insn.owner
                            while(currentClass != "java/lang/Object") {
                                @Suppress("LABEL_NAME_CLASH")
                                val clazz = nodes.firstOrNull { it.name == currentClass } ?: return@forEach
                                val method = clazz.methods.firstOrNull { it.name == insn.name && it.desc == insn.desc }
                                if(method == null) {
                                    println("Checking superclass: " + clazz.superName)
                                    currentClass = clazz.superName
                                } else {
                                    methods[insn] = method
                                    @Suppress("LABEL_NAME_CLASH")
                                    return@forEach
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
