package de.florianmichael.clampclient.injection.instrumentation_1_12;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.misc.ModuleNoPitchLimit;

public class UnboxedEntity {

    public static void setAngles(final Entity origin, float yaw, float pitch) {
        final float f = origin.getPitch();
        final float f1 = origin.getYaw();

        origin.setYaw((float)((double)origin.getYaw() + (double)yaw * 0.15D));
        origin.setPitch((float)((double)origin.getPitch() - (double)pitch * 0.15D));

        if(!TarasandeMain.Companion.managerModule().get(ModuleNoPitchLimit.class).getEnabled())
            origin.setPitch(MathHelper.clamp(origin.getPitch(), -90.0F, 90.0F));

        origin.prevPitch += origin.getPitch() - f;
        origin.prevYaw += origin.getYaw() - f1;
    }
}
