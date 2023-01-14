package net.tarasandedevelopment.tarasande.system.base.grabbersystem.impl

import net.tarasandedevelopment.tarasande.system.base.grabbersystem.Grabber
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldInsnNode
import org.objectweb.asm.tree.LdcInsnNode

class GrabberDefaultFlightSpeed : Grabber("net.minecraft.entity.player.PlayerAbilities", 0.05F) {
    override fun transform(classNode: ClassNode) {
        constant = findClassInitializer(classNode)
            .instructions
            .filterIsInstance<FieldInsnNode>()
            .first { it.name == resolveFieldMapping(classNode.name, "flySpeed") && it.owner == classNode.name }
            .previous
            .asType<LdcInsnNode>()
            .cst as Float
    }
}