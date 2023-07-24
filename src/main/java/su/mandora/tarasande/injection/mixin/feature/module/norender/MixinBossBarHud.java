package su.mandora.tarasande.injection.mixin.feature.module.norender;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.BossBarHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule;
import su.mandora.tarasande.system.feature.modulesystem.impl.render.ModuleNoRender;

@Mixin(BossBarHud.class)
public class MixinBossBarHud {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void noRender_render(DrawContext context, CallbackInfo ci) {
        if (ManagerModule.INSTANCE.get(ModuleNoRender.class).getHud().getBossBar().should()) {
            ci.cancel();
        }
    }
}
