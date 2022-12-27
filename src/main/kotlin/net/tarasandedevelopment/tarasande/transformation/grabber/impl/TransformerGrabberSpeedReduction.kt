package net.tarasandedevelopment.tarasande.transformation.grabber.impl

import net.tarasandedevelopment.tarasande.transformation.grabber.TransformerGrabber
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode

class TransformerGrabberSpeedReduction : TransformerGrabber("net.minecraft.entity.player.PlayerEntity", 0.6) {
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
            .asLDC()
            .cst as Double
    }
}