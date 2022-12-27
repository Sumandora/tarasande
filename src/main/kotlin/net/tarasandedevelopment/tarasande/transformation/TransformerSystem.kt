package net.tarasandedevelopment.tarasande.transformation

import net.tarasandedevelopment.tarasande.Manager
import net.tarasandedevelopment.tarasande.transformation.grabber.ManagerGrabber
import net.tarasandedevelopment.tarasande.transformation.mappings.TinyMappings
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.MethodNode

object ManagerTransformer : Manager<Transformer>() {

    val managerGrabber = ManagerGrabber(this)
    val tinyMappings = TinyMappings()

    fun transform(classNode: ClassNode) {
        list.forEach { transformer ->
            if (transformer.targetedClasses.any {
                    println(transformer.resolveClassMapping(classNode.name) + " == $it")
                    transformer.resolveClassMapping(classNode.name) == it.replace(".", "/")
                }) {
                println("$transformer transforming")
                transformer.transform(classNode)
                }
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
            println(resolveMethodMapping(classNode.name, it.name, it.desc) + " == " + name)
            resolveMethodMapping(classNode.name, it.name, it.desc) == name
        }
    }

    fun findField(classNode: ClassNode, name: String): FieldNode {
        return classNode.fields.first {
            resolveFieldMapping(classNode.name, it.name) == name
        }
    }
}