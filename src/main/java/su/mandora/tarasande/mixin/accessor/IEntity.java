package su.mandora.tarasande.mixin.accessor;

import net.minecraft.util.math.Vec3d;

public interface IEntity {
    Vec3d invokeGetRotationVector(float pitch, float yaw);
}
