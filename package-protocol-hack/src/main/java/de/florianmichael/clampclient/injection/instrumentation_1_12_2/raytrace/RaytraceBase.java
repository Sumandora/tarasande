package de.florianmichael.clampclient.injection.instrumentation_1_12_2.raytrace;

import de.florianmichael.clampclient.injection.instrumentation_1_12_2.model.ViaRaytraceResult;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public abstract class RaytraceBase {

    public abstract Vec3d getPositionEyes(Entity entity, float partialTicks);
    public abstract ViaRaytraceResult raytrace(Entity entity, float prevYaw, float prevPitch, float yaw, float pitch, float partialTicks);
}
