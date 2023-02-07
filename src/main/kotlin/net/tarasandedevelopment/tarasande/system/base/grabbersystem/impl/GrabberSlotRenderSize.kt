package net.tarasandedevelopment.tarasande.system.base.grabbersystem.impl

import net.tarasandedevelopment.tarasande.system.base.grabbersystem.Grabber
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.IntInsnNode

class GrabberSlotRenderSize : Grabber("net.minecraft.client.gui.screen.ingame.HandledScreen", 16) {

    override fun transform(classNode: ClassNode) {
        constant = findMethod(classNode, "isPointOverSlot")
            .instructions
            .first { it.opcode == Opcodes.BIPUSH }
            .asType<IntInsnNode>()
            .operand
    }
}