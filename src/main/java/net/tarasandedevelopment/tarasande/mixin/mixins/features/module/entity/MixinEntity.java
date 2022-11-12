package net.tarasandedevelopment.tarasande.mixin.mixins.features.module.entity;

import net.minecraft.entity.Entity;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.impl.render.ModuleESP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;

@Mixin(Entity.class)
public class MixinEntity {
    @Inject(method = "getTeamColorValue", at = @At("RETURN"), cancellable = true)
    public void hookESP(CallbackInfoReturnable<Integer> cir) {
        ModuleESP moduleESP = TarasandeMain.Companion.get().getModuleSystem().get(ModuleESP.class);
        if (moduleESP.getEnabled()) {
            Color c = moduleESP.getEntityColor().getColor((Entity) (Object) this);
            if (c != null)
                cir.setReturnValue(c.getRGB());
        }
    }
}
