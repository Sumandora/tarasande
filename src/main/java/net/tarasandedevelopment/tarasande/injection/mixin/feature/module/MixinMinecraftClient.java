package net.tarasandedevelopment.tarasande.injection.mixin.feature.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ManagerModule;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.render.ModuleESP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {

    @Redirect(method = "hasOutline", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isGlowing()Z"))
    public boolean hookESP(Entity entity) {
        boolean glowing = entity.isGlowing();
        ModuleESP moduleESP = ManagerModule.INSTANCE.get(ModuleESP.class);
        if (moduleESP.getEnabled())
            return glowing || (moduleESP.getMode().isSelected(0) && moduleESP.shouldRender(entity));
        return glowing;
    }
}
