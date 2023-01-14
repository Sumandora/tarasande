package net.tarasandedevelopment.tarasande.system.base.grabbersystem.impl

import net.tarasandedevelopment.tarasande.system.base.grabbersystem.Grabber
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode

class GrabberServerInformationOffset : Grabber("net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget\$ServerEntry", expected = 5) {

    private val offsetCode = arrayOf(
        /*
            ILOAD 20
            ILOAD 5
            BIPUSH 15
            ISUB
            IF_ICMPLT L72
            ILOAD 20
            ILOAD 5
         */
        Opcodes.ILOAD,
        Opcodes.ILOAD,
        Opcodes.BIPUSH,
        Opcodes.ISUB,
        Opcodes.IF_ICMPLT,
        Opcodes.ILOAD,
        Opcodes.ILOAD
        //Opcodes.ICONST_5 // Target
    )


    override fun transform(classNode: ClassNode) {
        if(findMethod(classNode, "render")
            .instructions
            .matchSignature(offsetCode)
            .next(7)
            .opcode != Opcodes.ICONST_5)
            constant = null
        constant = 5
    }
}