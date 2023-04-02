package su.mandora.tarasande.injection.mixin.feature.module;

import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.option.Perspective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule;
import su.mandora.tarasande.system.feature.modulesystem.impl.render.ModuleFreeCam;

@Mixin(InGameHud.class)
public class MixinInGameHud {

    @Redirect(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/Perspective;isFirstPerson()Z"))
    public boolean hookFreeCam(Perspective instance) {
        if (ManagerModule.INSTANCE.get(ModuleFreeCam.class).getEnabled().getValue())
            return true;
        return instance.isFirstPerson();
    }

}
