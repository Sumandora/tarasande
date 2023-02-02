package net.tarasandedevelopment.tarasande.system.base.grabbersystem.impl

import net.tarasandedevelopment.tarasande.system.base.grabbersystem.Grabber
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.IntInsnNode

class GrabberScrollbarWidth : Grabber("net.minecraft.client.gui.widget.EntryListWidget", 6) {
    override fun transform(classNode: ClassNode) {
        constant = findMethod(classNode, "updateScrollingState")
            .instructions
            .last { it.opcode == Opcodes.BIPUSH }
            .asType<IntInsnNode>()
            .operand
    }
}