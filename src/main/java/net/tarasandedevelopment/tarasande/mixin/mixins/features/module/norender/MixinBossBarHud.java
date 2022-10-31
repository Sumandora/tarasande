package net.tarasandedevelopment.tarasande.mixin.mixins.features.module.norender;

import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.util.math.MatrixStack;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.features.module.render.ModuleNoRender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BossBarHud.class)
public class MixinBossBarHud {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void noRender_render(MatrixStack matrices, CallbackInfo ci) {
        if (TarasandeMain.Companion.get().getManagerModule().get(ModuleNoRender.class).getHud().getBossBar().should()) {
            ci.cancel();
        }
    }
}
