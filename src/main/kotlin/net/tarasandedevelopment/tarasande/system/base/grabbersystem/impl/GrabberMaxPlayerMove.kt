package net.tarasandedevelopment.tarasande.system.base.grabbersystem.impl

import net.tarasandedevelopment.tarasande.system.base.grabbersystem.Grabber
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.LdcInsnNode

class GrabberMaxPlayerMove : Grabber("net.minecraft.server.network.ServerPlayNetworkHandler", 100.0F) {

    private val maxMovementCode = arrayOf(
        /*
            ALOAD 0
            GETFIELD net/minecraft/server/network/ServerPlayNetworkHandler.player : Lnet/minecraft/server/network/ServerPlayerEntity;
            INVOKEVIRTUAL net/minecraft/server/network/ServerPlayerEntity.isFallFlying ()Z
            IFEQ L46
            LDC 300.0
            GOTO L47
            L46
            FRAME SAME
            LDC 100.0
         */
        Opcodes.ALOAD,
        Opcodes.GETFIELD,
        Opcodes.INVOKEVIRTUAL,
        Opcodes.IFEQ,
        Opcodes.LDC,
        Opcodes.GOTO,
        -1, // LABEL
        -1, // FRAME SAME
        Opcodes.LDC
    )

    override fun transform(classNode: ClassNode) {
        constant = findMethod(classNode, "onPlayerMove", reverseClassMapping("net.minecraft.network.listener.ServerPlayPacketListener"))
            .instructions
            .matchSignature(maxMovementCode)
            .next(8)
            .asType<LdcInsnNode>()
            .cst as Float
    }
}