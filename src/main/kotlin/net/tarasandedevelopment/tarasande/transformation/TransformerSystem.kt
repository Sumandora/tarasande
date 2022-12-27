package net.tarasandedevelopment.tarasande.transformation

import net.tarasandedevelopment.tarasande.Manager
import net.tarasandedevelopment.tarasande.transformation.grabber.ManagerGrabber
import net.tarasandedevelopment.tarasande.transformation.mapping.TinyMappings
import org.objectweb.asm.tree.*

object ManagerTransformer : Manager<Transformer>() {

    val managerGrabber = ManagerGrabber(this)
    val tinyMappings = TinyMappings()

    fun transform(classNode: ClassNode) {
        println(classNode.name)
        list.forEach { transformer ->
            if (transformer.targetedClasses.any {
                    transformer.resolveClassMapping(classNode.name) == it.replace(".", "/")
                })
                transformer.transform(classNode)
        }
    }

}

abstract class Transformer(vararg val targetedClasses: String) {
    abstract fun transform(classNode: ClassNode)

    fun resolveMethodMapping(owner: String, name: String, desc: String): String? {
        return ManagerTransformer.tinyMappings.mapMethodName(owner.replace(".", "/"), name, desc)
    }

    fun resolveFieldMapping(owner: String, name: String): String? {
        return ManagerTransformer.tinyMappings.mapFieldName(owner.replace(".", "/"), name)
    }

    fun resolveClassMapping(name: String): String? {
        return ManagerTransformer.tinyMappings.mapClassName(name.replace(".", "/"))
    }

    fun findMethod(classNode: ClassNode, name: String): MethodNode {
        return classNode.methods.first {
            resolveMethodMapping(classNode.name, it.name, it.desc) == name
        }
    }

    fun findClassInitializer(classNode: ClassNode): MethodNode {
        return classNode.methods.first { it.name == "<init>" }
    }

    fun findField(classNode: ClassNode, name: String): FieldNode {
        return classNode.fields.first {
            resolveFieldMapping(classNode.name, it.name) == name
        }
    }

    fun InsnList.matchSignature(signature: Array<Int>): AbstractInsnNode {
        for(index in 0 until size()) {
            var matches = true
            for(signatureIndex in signature.indices) {
                if(get(index + signatureIndex).opcode != signature[signatureIndex]) {
                    matches = false
                    break
                }
            }
            if(matches) {
                return get(index)
            }
        }
        error("Wasn't able to find signature in code")
    }

    fun AbstractInsnNode.next(amount: Int): AbstractInsnNode {
        var curr = this
        for(i in 0 until amount) {
            curr = curr.next
        }
        return curr
    }

    fun AbstractInsnNode.asLDC(): LdcInsnNode {
        return this as LdcInsnNode
    }

}