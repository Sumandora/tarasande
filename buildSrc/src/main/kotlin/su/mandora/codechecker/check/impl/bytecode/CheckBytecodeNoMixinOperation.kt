package su.mandora.codechecker.check.impl.bytecode

import su.mandora.codechecker.check.CheckBytecode

class CheckBytecodeNoMixinOperation : CheckBytecode("No Mixin Operation") {
    override fun run() {
        allNodes().forEach { classNode ->
            if (classNode.invisibleAnnotations?.any { it.desc == "Lorg/spongepowered/asm/mixin/Mixin;" } != true)
                return@forEach

            classNode.methods.forEach {
                if (it.name == "<clinit>" || it.name == "<init>")
                    return@forEach
                if (it.name.startsWith("tarasande_"))
                    return@forEach
                if (it.name.startsWith("lambda$"))
                    return@forEach
                if (it.visibleAnnotations?.isEmpty() != false)
                    violation(classNode, it.name + it.desc + " has no Mixin-operation, please specify one using an annotation")
            }
        }
    }
}