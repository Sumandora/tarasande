package net.tarasandedevelopment.tarasande.injection.mixin.feature.module.entity;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.exploit.ModuleNoPitchLimit;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.render.ModuleESP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;

@Mixin(Entity.class)
public class MixinEntity {

    @Inject(method = "getTeamColorValue", at = @At("RETURN"), cancellable = true)
    public void hookESP(CallbackInfoReturnable<Integer> cir) {
        ModuleESP moduleESP = TarasandeMain.Companion.managerModule().get(ModuleESP.class);
        if (moduleESP.getEnabled()) {
            Color c = moduleESP.getEntityColor().getColor((Entity) (Object) this);
            if (c != null)
                cir.setReturnValue(c.getRGB());
        }
    }

    @Redirect(method = "changeLookDirection", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;clamp(FFF)F"))
    public float hookNoPitchLimit(float value, float min, float max) {
        if ((Object) this == MinecraftClient.getInstance().player)
            if (TarasandeMain.Companion.managerModule().get(ModuleNoPitchLimit.class).getEnabled())
                return value;
        return MathHelper.clamp(value, min, max);
    }

}
