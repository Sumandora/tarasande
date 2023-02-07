package net.tarasandedevelopment.tarasande.system.base.grabbersystem.impl

import net.tarasandedevelopment.tarasande.system.base.grabbersystem.Grabber
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.IntInsnNode

class GrabberDefaultListWidgetWidth : Grabber("net.minecraft.client.gui.widget.EntryListWidget", 220) {
    override fun transform(classNode: ClassNode) {
        constant = findMethod(classNode, "getRowWidth")
            .instructions
            .first { it.opcode == Opcodes.SIPUSH }
            .asType<IntInsnNode>()
            .operand
    }
}