package net.tarasandedevelopment.tarasande.transformation.grabber.impl

import net.tarasandedevelopment.tarasande.transformation.grabber.TransformerGrabber
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldInsnNode

class TransformerGrabberDefaultFlightSpeed : TransformerGrabber("net.minecraft.entity.player.PlayerAbilities", 0.05F) {
    override fun transform(classNode: ClassNode) {
        constant = findClassInitializer(classNode)
            .instructions
            .filterIsInstance<FieldInsnNode>()
            .first { it.name == resolveFieldMapping(classNode.name, "flySpeed") && it.owner == classNode.name }
            .previous
            .asLDC()
            .cst as Float
        println("$constant lotto")
    }
}