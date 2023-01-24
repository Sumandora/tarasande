package de.florianmichael.clampclient.injection.mixininterface;

import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

public interface IBox_Protocol {

    HitResult protocolhack_calculateIntercept(Vec3d vecA, Vec3d vecB);

}
