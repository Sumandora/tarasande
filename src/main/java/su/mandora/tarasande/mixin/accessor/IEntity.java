package su.mandora.tarasande.mixin.accessor;

import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

public interface IEntity {
    Vec3d invokeGetRotationVector(float pitch, float yaw);

    Random getRandom();

    void setRandom(Random random);

}
