package su.mandora.tarasande.mixin.accessor;

import net.minecraft.util.math.Vec3d;

public interface IVec3d {
    void tarasande_setX(double x);

    void tarasande_setY(double y);

    void tarasande_setZ(double z);

    void tarasande_copy(Vec3d other);
}
