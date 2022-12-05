package su.mandora.codechecker.check.impl.bytecode

import org.objectweb.asm.tree.MethodInsnNode
import su.mandora.codechecker.check.CheckBytecode

class CheckBytecodeUnnecessaryMainCall : CheckBytecode("Unnecessary main call") {
    override fun run() {
        allNodes().forEach { node ->
            if(node.name.split("/").last().startsWith("Manager")) {
                node.methods.forEach {
                    if(it.instructions.any {
                        if(it is MethodInsnNode) {
                            if(it.owner.equals("net/tarasandedevelopment/tarasande/TarasandeMain\$Companion"))
                                return@any it.name.startsWith("manager")
                        }
                            return@any false
                    })
                        violation(node, "Contains unnecessary TarasandeMain usages")
                }
            }
        }
    }
}