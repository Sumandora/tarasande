package net.tarasandedevelopment.tarasande.injection.mixin.feature.module;

import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.option.Perspective;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ManagerModule;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.render.ModuleFreeCam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(InGameHud.class)
public class MixinInGameHud {

    @Redirect(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/Perspective;isFirstPerson()Z"))
    public boolean hookFreeCam(Perspective instance) {
        if (ManagerModule.INSTANCE.get(ModuleFreeCam.class).getEnabled())
            return true;
        return instance.isFirstPerson();
    }

}
