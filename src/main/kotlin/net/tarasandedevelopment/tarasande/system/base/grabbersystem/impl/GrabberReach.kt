package net.tarasandedevelopment.tarasande.system.base.grabbersystem.impl

import net.tarasandedevelopment.tarasande.system.base.grabbersystem.Grabber
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode

class GrabberReach : Grabber("net.minecraft.client.render.GameRenderer", 9.0) {
    private val reachCode = arrayOf(
        /*
            ILOAD 6
            IFEQ L33
            DLOAD 17
            LDC 9.0
            DCMPL
            IFLE L33
         */
        Opcodes.ILOAD,
        Opcodes.IFEQ,
        Opcodes.DLOAD,
        Opcodes.LDC,        // Target
        Opcodes.DCMPL,
        Opcodes.IFLE
    )

    override fun transform(classNode: ClassNode) {
        constant = findMethod(classNode, "updateTargetedEntity")
            .instructions
            .matchSignature(reachCode)
            .next(3)
            .asLDC()
            .cst as Double
    }
}