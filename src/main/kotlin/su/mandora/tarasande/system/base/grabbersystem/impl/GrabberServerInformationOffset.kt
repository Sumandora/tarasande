package su.mandora.tarasande.system.base.grabbersystem.impl

import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import su.mandora.tarasande.system.base.grabbersystem.Grabber

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
        if(findMethod(classNode, "render", reverseClassMapping("net.minecraft.client.gui.widget.EntryListWidget\$Entry"))
            .instructions
            .matchSignature(offsetCode)
            .next(7)
            .opcode != Opcodes.ICONST_5)
            constant = null
        constant = expected
    }
}