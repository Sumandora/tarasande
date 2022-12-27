package net.tarasandedevelopment.tarasande.transformation.grabber.impl

import net.tarasandedevelopment.tarasande.transformation.grabber.Grabber
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.LdcInsnNode

class GrabberReach : Grabber("net.minecraft.client.render.GameRenderer") {
    override fun transform(classNode: ClassNode) {
        constant = findMethod(classNode, "updateTargetedEntity")
            .instructions
            .filterIsInstance<LdcInsnNode>()
            .last {
                it.cst is Double
            }.cst as Double
        println("Found constant: $constant")
    }
}