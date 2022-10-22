package net.tarasandedevelopment.tarasande.mixin.mixins.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.module.render.ModuleESP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {

    @Redirect(method = "hasOutline", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isGlowing()Z"))
    public boolean hookESP(Entity entity) {
        boolean glowing = entity.isGlowing();
        if (!TarasandeMain.Companion.get().getDisabled()) {
            ModuleESP moduleESP = TarasandeMain.Companion.get().getManagerModule().get(ModuleESP.class);
            if (moduleESP.getEnabled())
                return glowing || (moduleESP.getMode().isSelected(0) && moduleESP.filter(entity));
        }
        return glowing;
    }
}
