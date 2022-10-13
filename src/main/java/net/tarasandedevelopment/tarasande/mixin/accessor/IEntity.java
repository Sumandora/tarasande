package net.tarasandedevelopment.tarasande.mixin.accessor;

import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

public interface IEntity {
    Vec3d tarasande_invokeGetRotationVector(float pitch, float yaw);

    Random tarasande_getRandom();

    void tarasande_setRandom(Random random);

    Vec3d tarasande_invokeMovementInputToVelocity(Vec3d movementInput, float speed, float yaw);

    int tarasande_getSprintingFlagIndex();

    int tarasande_getInvisibleFlagIndex();

    boolean tarasande_forceGetFlag(int index);

}
