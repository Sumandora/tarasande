package net.tarasandedevelopment.tarasande.system.base.grabber.impl

import net.tarasandedevelopment.tarasande.system.base.grabber.Grabber
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldInsnNode

class TransformerGrabberDefaultFlightSpeed : Grabber("net.minecraft.entity.player.PlayerAbilities", 0.05F) {
    override fun transform(classNode: ClassNode) {
        constant = findClassInitializer(classNode)
            .instructions
            .filterIsInstance<FieldInsnNode>()
            .first { it.name == resolveFieldMapping(classNode.name, "flySpeed") && it.owner == classNode.name }
            .previous
            .asLDC()
            .cst as Float
    }
}