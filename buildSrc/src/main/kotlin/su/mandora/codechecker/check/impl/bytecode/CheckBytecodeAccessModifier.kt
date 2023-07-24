package su.mandora.codechecker.check.impl.bytecode

import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*
import su.mandora.codechecker.check.CheckBytecode
import java.util.regex.Pattern

class CheckBytecodeAccessModifier : CheckBytecode("Access Modifier") {

    private val lambdaNames = Pattern.compile("[^*]*\\$\\d+$")

    override fun run() {
        allNodes().forEach { clazz ->
            if (clazz.name.matches(lambdaNames.toRegex()))
                return@forEach // We don't have control over lambdas
            if (clazz.access and Opcodes.ACC_INTERFACE == Opcodes.ACC_INTERFACE)
                return@forEach // Interfaces have everything public no matter what

            clazz.fields.forEach { field ->
                if (field.visibleAnnotations?.any { it.desc == "Lorg/spongepowered/asm/mixin/Shadow;" } == true)
                    return@forEach // Shadowed variables inherit the original access modifiers
                if (field.access and Opcodes.ACC_SYNTHETIC == Opcodes.ACC_SYNTHETIC)
                    return@forEach // The compiler made them synthetic for a reason
                if (AccessModifier.values().none { mod -> mod.matches(field.access) }) {
                    violation(clazz, field.name + field.desc + " has no access modifier, this does implicitly force private access, but is not recommended")
                    return@forEach
                }

                val allUsages = allUsages(clazz, field.name, field.desc)
                val intendedAccessModifier =
                    when {
                        allUsages.all { it.key == clazz } -> AccessModifier.PRIVATE
                        allUsages.all { allInheritingClasses(clazz).contains(it.key) } -> AccessModifier.PROTECTED
                        else -> AccessModifier.PUBLIC
                    }

                if (!intendedAccessModifier.matches(field.access)) {
                    violation(clazz, field.name + field.desc + " does not match the intended access modifier (" + AccessModifier.values().first { it.matches(field.access) } + " != " + intendedAccessModifier + ")")
                }
            }
            clazz.methods.forEach { method ->
                if (method.access and Opcodes.ACC_SYNTHETIC == Opcodes.ACC_SYNTHETIC)
                    return@forEach // The compiler made them synthetic for a reason
                if (method.name.startsWith("<") && method.name.endsWith(">"))
                    return@forEach // Initializers are cool
                if (isInherited(clazz, method)) {
                    println(clazz.name + " " + method.name + " was ignored")
                    return@forEach
                }
                if (AccessModifier.values().none { mod -> mod.matches(method.access) }) {
                    violation(clazz, method.name + method.desc + " has no access modifier, this does implicitly force private access, but is not recommended")
                    return@forEach
                }

                val allUsages = allUsages(clazz, method.name, method.desc)
                val intendedAccessModifier =
                    when {
                        allUsages.all { it.key == clazz } -> AccessModifier.PRIVATE
                        allUsages.all { allInheritingClasses(clazz).contains(it.key) } -> AccessModifier.PROTECTED
                        else -> AccessModifier.PUBLIC
                    }

                if (!intendedAccessModifier.matches(method.access)) {
                    violation(clazz, method.name + method.desc + " does not match the intended access modifier (" + AccessModifier.values().first { it.matches(method.access) } + " != " + intendedAccessModifier + ")")
                }
            }
        }
    }

    private fun isInherited(clazz: ClassNode, method: MethodNode): Boolean {
        return allSuperClasses(clazz).any { it.methods.any { it.name == method.name && it.desc == method.desc } }
    }

    private fun allSuperClasses(clazz: ClassNode): ArrayList<ClassNode> {
        val classes = ArrayList<ClassNode>()
        var clazz: ClassNode? = clazz
        while (true) {
            clazz = allNodes().firstOrNull { clazz?.superName == it.name }
            classes.add(clazz ?: break)
        }
        return classes
    }

    private fun allInheritingClasses(clazz: ClassNode): ArrayList<ClassNode> {
        val open = ArrayList<ClassNode>()
        open.add(clazz)
        val closed = ArrayList<ClassNode>()
        while (open.isNotEmpty()) {
            val first = open.removeFirst()
            if (closed.contains(first))
                continue
            closed.add(first)
            open.addAll(allNodes().filter { it.superName == first.name || it.interfaces.any { it == first.name } })
        }
        return closed
    }

    private fun allUsages(clazz: ClassNode, name: String, desc: String): HashMap<ClassNode, ArrayList<AbstractInsnNode>> {
        val owners = allInheritingClasses(clazz).map { it.name }
        val map = HashMap<ClassNode, ArrayList<AbstractInsnNode>>()
        allNodes().forEach { newClass ->
            newClass.methods.forEach { method ->
                method.instructions.forEach { insn ->
                    when (insn) {
                        is FieldInsnNode -> {
                            if (owners.any { it == insn.owner } && insn.name == name && insn.desc == desc)
                                map.computeIfAbsent(newClass) { ArrayList() }.add(insn)
                        }

                        is MethodInsnNode -> {
                            if (owners.any { it == insn.owner } && insn.name == name && insn.desc == desc)
                                map.computeIfAbsent(newClass) { ArrayList() }.add(insn)
                        }
                    }
                }
            }
        }
        return map
    }

    enum class AccessModifier(private val opcode: Int) {
        PRIVATE(Opcodes.ACC_PRIVATE), PROTECTED(Opcodes.ACC_PROTECTED), PUBLIC(Opcodes.ACC_PUBLIC);

        fun matches(access: Int): Boolean {
            return access and opcode == opcode
        }
    }
}