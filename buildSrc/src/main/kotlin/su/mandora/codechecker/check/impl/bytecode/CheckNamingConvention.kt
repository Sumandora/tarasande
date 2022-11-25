package su.mandora.codechecker.check.impl.bytecode

import org.objectweb.asm.Type
import su.mandora.codechecker.check.CheckBytecode
import java.util.regex.Pattern

class CheckNamingConvention : CheckBytecode("Naming convention") {

    val lambdaNames = Pattern.compile("[^*]*\\$\\d+$")

    override fun run() {
        allNodes().forEach { classNode ->
            if (lambdaNames.asPredicate().test(classNode.name))
                return@forEach // Lambdas don't have names duh (yes, even when kotlin adds some garbled bs, we couldn't care less)

            val actualName = classNode.name.split("/").last().split("$").last() // packages don't matter

            if (classNode.name.startsWith("net/tarasandedevelopment/tarasande/mixin/mixins")) {
                // Mixins
                if (classNode.invisibleAnnotations == null || classNode.invisibleAnnotations.isEmpty())
                    violation(classNode, "Is not a Mixin class")
                else {
                    val pseudo = classNode.invisibleAnnotations.find { it.desc == "Lorg/spongepowered/asm/mixin/Pseudo;" }
                    val mixin = classNode.invisibleAnnotations.find { it.desc == "Lorg/spongepowered/asm/mixin/Mixin;" } ?: return@forEach

                    if (pseudo != null) {
                        val pseudoIndex = classNode.invisibleAnnotations.indexOf(pseudo)
                        if (pseudoIndex != 0 || pseudoIndex + 1 != classNode.invisibleAnnotations.indexOf(mixin))
                            violation(classNode, "First annotation should be @Pseudo, followed with @Mixin")
                    }

                    val values = mixin.values
                            if (values == null || values.isEmpty()) {
                                violation(classNode, "Mixin has no target")
                                return@forEach // We can't process an invalid class
                            }

                    @Suppress("UNCHECKED_CAST")
                    var targetClass =
                        when (values.first()) {
                            "targets" -> {
                                (values[1] as List<String>).first().replace(".", "/")
                            }
                            "value" -> {
                                (values[1] as List<Type>).first().internalName.let { it.substring(1, it.length - 1) }
                            }
                            else -> {
                                violation(classNode, "Target class is not the first key")
                                return@forEach // This mixin is invalid...
                            }
                        }

                    targetClass = targetClass.split("/").last()

                    if (!targetClass.split("$").any { actualName.startsWith("Mixin$it") })
                        violation(classNode, "Mixin has a invalid name")
                }

            } else {
                // General

                if (classNode.superName == "java/lang/Object" || classNode.superName == "java/lang/Enum")
                    return@forEach // Ignore non-extending classes

                val actualSuperName = classNode.superName.split("/").last().split("$").last()
                if (!actualName.startsWith(actualSuperName))
                    violation(classNode, "Wrong naming conventions (Did you mean '$actualSuperName$actualName'?)")
            }
        }
    }
}