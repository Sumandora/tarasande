package su.mandora.tarasande.mixin.accessor;

import net.minecraft.util.math.Vec3d;

public interface IVec3d {
    void setX(double x);
    void setY(double y);
    void setZ(double z);
    void copy(Vec3d other);
}
