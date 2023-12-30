package su.mandora.tarasande.injection.mixin.feature.module;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule;
import su.mandora.tarasande.system.feature.modulesystem.impl.render.ModuleFreeCam;

@Mixin(InGameHud.class)
public class MixinInGameHud {

    @ModifyExpressionValue(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/Perspective;isFirstPerson()Z"))
    public boolean hookFreeCam(boolean original) {
        return original || ManagerModule.INSTANCE.get(ModuleFreeCam.class).getEnabled().getValue();
    }

}
