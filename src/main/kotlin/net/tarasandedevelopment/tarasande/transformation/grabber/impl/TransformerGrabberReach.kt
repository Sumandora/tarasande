package net.tarasandedevelopment.tarasande.transformation.grabber.impl

import net.tarasandedevelopment.tarasande.transformation.grabber.TransformerGrabber
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.LdcInsnNode

class TransformerGrabberReach : TransformerGrabber("net.minecraft.client.render.GameRenderer", 9.0) {
    override fun transform(classNode: ClassNode) {
        constant = findMethod(classNode, "updateTargetedEntity")
            .instructions
            .filterIsInstance<LdcInsnNode>()
            .last()
            .asLDC()
            .cst as Double
    }
}