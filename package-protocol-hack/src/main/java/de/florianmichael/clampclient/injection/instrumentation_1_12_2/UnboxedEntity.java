package de.florianmichael.clampclient.injection.instrumentation_1_12_2;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ManagerModule;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.exploit.ModuleNoPitchLimit;

public class UnboxedEntity {

    public static void setAngles(final Entity origin, float yaw, float pitch) {
        final float f = origin.getPitch();
        final float f1 = origin.getYaw();

        origin.setYaw((float)((double)origin.getYaw() + (double)yaw * 0.15D));
        origin.setPitch((float)((double)origin.getPitch() - (double)pitch * 0.15D));

        if(!ManagerModule.INSTANCE.get(ModuleNoPitchLimit.class).getEnabled().getValue())
            origin.setPitch(MathHelper.clamp(origin.getPitch(), -90.0F, 90.0F));

        origin.prevPitch += origin.getPitch() - f;
        origin.prevYaw += origin.getYaw() - f1;
    }
}
