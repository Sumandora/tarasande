package net.tarasandedevelopment.tarasande.system.base.grabbersystem.impl

import net.tarasandedevelopment.tarasande.system.base.grabbersystem.Grabber
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.LdcInsnNode

class GrabberSpeedReduction : Grabber("net.minecraft.entity.player.PlayerEntity", 0.6) {
    private val reductionCode = arrayOf(
        /*
            ALOAD 0
            ALOAD 0
            INVOKEVIRTUAL net/minecraft/entity/player/PlayerEntity.getVelocity ()Lnet/minecraft/util/math/Vec3d;
            LDC 0.6
            DCONST_1
            LDC 0.6
            INVOKEVIRTUAL net/minecraft/util/math/Vec3d.multiply (DDD)Lnet/minecraft/util/math/Vec3d;
            INVOKEVIRTUAL net/minecraft/entity/player/PlayerEntity.setVelocity (Lnet/minecraft/util/math/Vec3d;)V
         */
        Opcodes.ALOAD,          // Signature Index
        Opcodes.ALOAD,
        Opcodes.INVOKEVIRTUAL,
        Opcodes.LDC,            // Target (Offset 3)
        Opcodes.DCONST_1,
        Opcodes.LDC,
        Opcodes.INVOKEVIRTUAL,
        Opcodes.INVOKEVIRTUAL
    )

    override fun transform(classNode: ClassNode) {
        constant = findMethod(classNode, "attack")
            .instructions
            .matchSignature(reductionCode)
            .next(3)
            .asType<LdcInsnNode>()
            .cst as Double
    }
}