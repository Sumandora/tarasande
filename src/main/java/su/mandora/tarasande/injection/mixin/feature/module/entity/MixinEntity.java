package su.mandora.tarasande.injection.mixin.feature.module.entity;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule;
import su.mandora.tarasande.system.feature.modulesystem.impl.misc.ModuleNoPitchLimit;
import su.mandora.tarasande.system.feature.modulesystem.impl.render.ModuleESP;
import su.mandora.tarasande.system.feature.modulesystem.impl.render.ModuleTrueSight;

import java.awt.*;

@Mixin(Entity.class)
public class MixinEntity {

    @Inject(method = "getTeamColorValue", at = @At("RETURN"), cancellable = true)
    public void hookESP(CallbackInfoReturnable<Integer> cir) {
        ModuleESP moduleESP = ManagerModule.INSTANCE.get(ModuleESP.class);
        if (moduleESP.getEnabled().getValue()) {
            Color c = moduleESP.getEntityColor().getColor((Entity) (Object) this);
            if (c != null)
                cir.setReturnValue(c.getRGB());
        }
    }

    @Redirect(method = "changeLookDirection", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;clamp(FFF)F"))
    public float hookNoPitchLimit(float value, float min, float max) {
        if ((Object) this == MinecraftClient.getInstance().player)
            if (ManagerModule.INSTANCE.get(ModuleNoPitchLimit.class).getEnabled().getValue())
                return value;
        return MathHelper.clamp(value, min, max);
    }

    @Inject(method = "isInvisible", at = @At("HEAD"), cancellable = true)
    public void hookTrueSight_render(CallbackInfoReturnable<Boolean> cir) {
        if (ManagerModule.INSTANCE.get(ModuleTrueSight.class).getEnabled().getValue())
            cir.setReturnValue(false);
    }

}
