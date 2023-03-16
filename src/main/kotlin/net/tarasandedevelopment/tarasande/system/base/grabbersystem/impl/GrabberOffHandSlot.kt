package net.tarasandedevelopment.tarasande.system.base.grabbersystem.impl

import net.tarasandedevelopment.tarasande.system.base.grabbersystem.Grabber
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.IntInsnNode

class GrabberOffHandSlot : Grabber("net.minecraft.screen.PlayerScreenHandler", Pair(45, 40)) {
    private val offhandSlotQuickMoveCode = arrayOf(
        /*
            ALOAD 6
            GETSTATIC net/minecraft/entity/EquipmentSlot.OFFHAND : Lnet/minecraft/entity/EquipmentSlot;
            IF_ACMPNE L24
            ALOAD 0
            GETFIELD net/minecraft/screen/PlayerScreenHandler.slots : Lnet/minecraft/util/collection/DefaultedList;
            BIPUSH 45
            INVOKEVIRTUAL net/minecraft/util/collection/DefaultedList.get (I)Ljava/lang/Object;
            CHECKCAST net/minecraft/screen/slot/Slot
            INVOKEVIRTUAL net/minecraft/screen/slot/Slot.hasStack ()Z
            IFNE L24
         */
        Opcodes.ALOAD,
        Opcodes.GETSTATIC,
        Opcodes.IF_ACMPNE,
        Opcodes.ALOAD,
        Opcodes.GETFIELD,
        Opcodes.BIPUSH,
        Opcodes.INVOKEVIRTUAL,
        Opcodes.CHECKCAST,
        Opcodes.INVOKEVIRTUAL,
        Opcodes.IFNE
    )

    private val offhandSlotCreationCode = arrayOf(
        /*
            ALOAD 0
            NEW net/minecraft/screen/PlayerScreenHandler$2
            DUP
            ALOAD 0
            ALOAD 1
            BIPUSH 40
            BIPUSH 77
            BIPUSH 62
            INVOKESPECIAL net/minecraft/screen/PlayerScreenHandler$2.<init> (Lnet/minecraft/screen/PlayerScreenHandler;Lnet/minecraft/inventory/Inventory;III)V
            INVOKEVIRTUAL net/minecraft/screen/PlayerScreenHandler.addSlot (Lnet/minecraft/screen/slot/Slot;)Lnet/minecraft/screen/slot/Slot;
            POP
         */
        Opcodes.ALOAD,
        Opcodes.NEW,
        Opcodes.DUP,
        Opcodes.ALOAD,
        Opcodes.ALOAD,
        Opcodes.BIPUSH,
        Opcodes.BIPUSH,
        Opcodes.BIPUSH,
        Opcodes.INVOKESPECIAL,
        Opcodes.INVOKEVIRTUAL,
        Opcodes.POP
    )

    override fun transform(classNode: ClassNode) {
        constant = expected
//        constant = Pair(
//            findMethod(classNode, "quickMove", reverseClassMapping("net.minecraft.screen.ScreenHandler"))
//                .instructions
//                .matchSignature(offhandSlotQuickMoveCode)
//                .next(5)
//                .asType<IntInsnNode>()
//                .operand,
//            findClassInitializer(classNode)
//                .instructions
//                .matchSignature(offhandSlotCreationCode)
//                .next(5)
//                .asType<IntInsnNode>()
//                .operand
//        )
    }

}
