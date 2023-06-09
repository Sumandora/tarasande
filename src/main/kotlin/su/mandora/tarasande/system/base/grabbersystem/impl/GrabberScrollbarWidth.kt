package su.mandora.tarasande.system.base.grabbersystem.impl

import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.IntInsnNode
import su.mandora.tarasande.system.base.grabbersystem.Grabber

class GrabberScrollbarWidth : Grabber("net.minecraft.client.gui.widget.EntryListWidget", 6) {
    override fun transform(classNode: ClassNode) {
        constant = findMethod(classNode, "updateScrollingState")
            .instructions
            .last { it.opcode == Opcodes.BIPUSH }
            .asType<IntInsnNode>()
            .operand
    }
}