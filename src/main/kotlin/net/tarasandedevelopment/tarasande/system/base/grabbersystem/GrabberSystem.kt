package net.tarasandedevelopment.tarasande.system.base.grabbersystem

import net.tarasandedevelopment.tarasande.Manager
import net.tarasandedevelopment.tarasande.system.base.grabbersystem.impl.GrabberDefaultFlightSpeed
import net.tarasandedevelopment.tarasande.system.base.grabbersystem.impl.GrabberReach
import net.tarasandedevelopment.tarasande.system.base.grabbersystem.impl.GrabberSpeedReduction
import net.tarasandedevelopment.tarasande.system.base.grabbersystem.mapping.TinyMappings
import org.objectweb.asm.ClassReader
import org.objectweb.asm.tree.*
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import java.util.jar.Attributes
import java.util.jar.Manifest
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class ManagerGrabber : Manager<Grabber>() {

    init {
        add(
            GrabberReach(),
            GrabberSpeedReduction(),
            GrabberDefaultFlightSpeed()
        )

        val minecraftJar = System.getProperty("java.class.path")
            .split(File.pathSeparatorChar)
            .map { File(it) }
            .filter { !it.isDirectory && it.extension == "jar" }
            .first { f ->
                val zis = ZipInputStream(FileInputStream(f))
                var entry: ZipEntry?
                var correct = false
                while (zis.nextEntry.also { entry = it } != null) {
                    if (entry?.name.equals("META-INF/MANIFEST.MF")) {
                        val main = Manifest(ByteArrayInputStream(zis.readAllBytes()))
                            .mainAttributes
                            .entries
                            .firstOrNull { it.key == Attributes.Name("Main-Class") }?.value
                        if (main == "net.minecraft.client.Main") {
                            correct = true
                            zis.close()
                            break
                        }
                    }
                    zis.closeEntry()
                }
                zis.close()
                return@first correct
            }

        val zis = ZipInputStream(FileInputStream(minecraftJar)) // We have to read it again, because we don't know at which entry we again, manifest -should- always be the first, but JVM accepts it if violated
        val classNodes = ArrayList<ClassNode>()
        var zipEntry: ZipEntry?
        while (zis.nextEntry.also { zipEntry = it } != null) {
            if (zipEntry!!.name.endsWith(".class")) {
                val node = ClassNode()
                ClassReader(zis.readAllBytes()).accept(node, ClassReader.EXPAND_FRAMES)
                classNodes.add(node)
            }
            zis.closeEntry()
        }
        zis.close()

        classNodes.forEach { classNode ->
            list.forEach { transformer ->
                if (transformer.resolveClassMapping(classNode.name) == transformer.targetedClass.replace(".", "/"))
                    transformer.transform(classNode)
            }
        }
    }

    fun getConstant(grabber: Class<out Grabber>): Any {
        return get(grabber).constant!!
    }

}

abstract class Grabber(val targetedClass: String, private val expected: Any) {
    var constant: Any? = null
        get() {
            if (field == null)
                error(javaClass.simpleName + " wasn't able to read their constant")
            return field
        }
        protected set(value) {
            if (value != expected)
                error(javaClass.simpleName + " read a different value than expected (Expected: $expected, but received $value)")
            field = value
        }

    abstract fun transform(classNode: ClassNode)

    fun resolveMethodMapping(owner: String, name: String, desc: String): String {
        return TinyMappings.mapMethodName(owner.replace(".", "/"), name, desc)
    }

    fun resolveFieldMapping(owner: String, name: String): String {
        return TinyMappings.mapFieldName(owner.replace(".", "/"), name)
    }

    fun resolveClassMapping(name: String): String {
        return TinyMappings.mapClassName(name.replace(".", "/"))
    }

    fun findMethod(classNode: ClassNode, name: String): MethodNode {
        return classNode.methods.first {
            resolveMethodMapping(classNode.name, it.name, it.desc) == name
        }
    }

    fun findClassInitializer(classNode: ClassNode): MethodNode {
        return classNode.methods.first { it.name == "<init>" }
    }

    fun findClassStaticInitializer(classNode: ClassNode): MethodNode {
        return classNode.methods.first { it.name == "<clinit>" }
    }

    fun findField(classNode: ClassNode, name: String): FieldNode {
        return classNode.fields.first {
            resolveFieldMapping(classNode.name, it.name) == name
        }
    }

    fun InsnList.matchSignature(signature: Array<Int>): AbstractInsnNode {
        for (index in 0 until size()) {
            var matches = true
            for (signatureIndex in signature.indices) {
                if (get(index + signatureIndex).opcode != signature[signatureIndex]) {
                    matches = false
                    break
                }
            }
            if (matches) {
                return get(index)
            }
        }
        error("Wasn't able to find signature in code")
    }

    fun AbstractInsnNode.next(amount: Int): AbstractInsnNode {
        var curr = this
        for (i in 0 until amount) {
            curr = curr.next
        }
        return curr
    }

    fun AbstractInsnNode.asLDC(): LdcInsnNode {
        return this as LdcInsnNode
    }
}
