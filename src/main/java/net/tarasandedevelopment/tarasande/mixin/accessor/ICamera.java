package net.tarasandedevelopment.tarasande.mixin.accessor;

import net.minecraft.util.math.Vec3d;

public interface ICamera {

    void tarasande_invokeSetPos(Vec3d pos);

    void tarasande_invokeSetRotation(float yaw, float pitch);

}
