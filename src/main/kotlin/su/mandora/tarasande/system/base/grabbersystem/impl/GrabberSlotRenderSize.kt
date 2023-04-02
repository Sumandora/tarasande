package su.mandora.tarasande.system.base.grabbersystem.impl

import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.IntInsnNode
import su.mandora.tarasande.system.base.grabbersystem.Grabber

class GrabberSlotRenderSize : Grabber("net.minecraft.client.gui.screen.ingame.HandledScreen", 16) {

    override fun transform(classNode: ClassNode) {
        constant = findMethod(classNode, "isPointOverSlot")
            .instructions
            .first { it.opcode == Opcodes.BIPUSH }
            .asType<IntInsnNode>()
            .operand
    }
}