package su.mandora.tarasande.injection.mixin.feature.module.norender;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.ScoreboardObjective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule;
import su.mandora.tarasande.system.feature.modulesystem.impl.render.ModuleNoRender;

@Mixin(InGameHud.class)
public class MixinInGameHud {

    @Inject(method = "renderPortalOverlay", at = @At("HEAD"), cancellable = true)
    public void noRender_renderPortalOverlay(DrawContext context, float nauseaStrength, CallbackInfo ci) {
        if (ManagerModule.INSTANCE.get(ModuleNoRender.class).getOverlay().getPortalOverlay().should()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderSpyglassOverlay", at = @At("HEAD"), cancellable = true)
    public void noRender_renderSpyglassOverlay(DrawContext context, float scale, CallbackInfo ci) {
        if (ManagerModule.INSTANCE.get(ModuleNoRender.class).getOverlay().getSpyglassOverlay().should()) {
            ci.cancel();
        }
    }

    @ModifyArgs(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderOverlay(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/util/Identifier;F)V", ordinal = 0))
    public void noRender_render_pumpkin(Args args) {
        if (ManagerModule.INSTANCE.get(ModuleNoRender.class).getOverlay().getPumpkinOverlay().should()) {
            args.set(2, 0F);
        }
    }

    @ModifyArgs(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderOverlay(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/util/Identifier;F)V", ordinal = 1))
    public void noRender_render_powderedSnow(Args args) {
        if (ManagerModule.INSTANCE.get(ModuleNoRender.class).getOverlay().getPowderedSnowOverlay().should()) {
            args.set(2, 0F);
        }
    }

    @Inject(method = "renderVignetteOverlay", at = @At("HEAD"), cancellable = true)
    public void noRender_renderVignetteOverlay(DrawContext context, Entity entity, CallbackInfo ci) {
        if (ManagerModule.INSTANCE.get(ModuleNoRender.class).getOverlay().getVignette().should()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    public void noRender_renderCrosshair(DrawContext context, CallbackInfo ci) {
        if (ManagerModule.INSTANCE.get(ModuleNoRender.class).getHud().getCrosshair().should()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderHeldItemTooltip", at = @At("HEAD"), cancellable = true)
    public void noRender_renderHeldItemTooltip(DrawContext context, CallbackInfo ci) {
        if (ManagerModule.INSTANCE.get(ModuleNoRender.class).getHud().getHeldItemName().should()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderScoreboardSidebar", at = @At("HEAD"), cancellable = true)
    public void noRender_renderScoreboardSidebar(DrawContext context, ScoreboardObjective objective, CallbackInfo ci) {
        if (ManagerModule.INSTANCE.get(ModuleNoRender.class).getHud().getScoreboard().should()) {
            ci.cancel();
        }
    }
}
