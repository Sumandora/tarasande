package net.tarasandedevelopment.tarasande.mixin.mixins.features.module.norender;

import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.impl.render.ModuleNoRender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(InGameHud.class)
public class MixinInGameHud {

    @Inject(method = "renderPortalOverlay", at = @At("HEAD"), cancellable = true)
    public void noRender_renderPortalOverlay(float nauseaStrength, CallbackInfo ci) {
        if (TarasandeMain.Companion.managerModule().get(ModuleNoRender.class).getOverlay().getPortalOverlay().should()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderSpyglassOverlay", at = @At("HEAD"), cancellable = true)
    public void noRender_renderSpyglassOverlay(float nauseaStrength, CallbackInfo ci) {
        if (TarasandeMain.Companion.managerModule().get(ModuleNoRender.class).getOverlay().getSpyglassOverlay().should()) {
            ci.cancel();
        }
    }

    @ModifyArgs(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderOverlay(Lnet/minecraft/util/Identifier;F)V", ordinal = 0))
    public void noRender_render_pumpkin(Args args) {
        if (TarasandeMain.Companion.managerModule().get(ModuleNoRender.class).getOverlay().getPumpkinOverlay().should()) {
            args.set(1, 0F);
        }
    }

    @ModifyArgs(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderOverlay(Lnet/minecraft/util/Identifier;F)V", ordinal = 1))
    public void noRender_render_powderedSnow(Args args) {
        if (TarasandeMain.Companion.managerModule().get(ModuleNoRender.class).getOverlay().getPowderedSnowOverlay().should()) {
            args.set(1, 0F);
        }
    }

    @Inject(method = "renderVignetteOverlay", at = @At("HEAD"), cancellable = true)
    public void noRender_renderVignetteOverlay(Entity entity, CallbackInfo ci) {
        if (TarasandeMain.Companion.managerModule().get(ModuleNoRender.class).getOverlay().getVignette().should()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    public void noRender_renderCrosshair(MatrixStack matrices, CallbackInfo ci) {
        if (TarasandeMain.Companion.managerModule().get(ModuleNoRender.class).getHud().getCrosshair().should()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderHeldItemTooltip", at = @At("HEAD"), cancellable = true)
    public void noRender_renderHeldItemTooltip(MatrixStack matrices, CallbackInfo ci) {
        if (TarasandeMain.Companion.managerModule().get(ModuleNoRender.class).getHud().getHeldItemName().should()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderStatusEffectOverlay", at = @At("HEAD"), cancellable = true)
    public void noRender_renderStatusEffectOverlay(MatrixStack matrices, CallbackInfo ci) {
        if (TarasandeMain.Companion.managerModule().get(ModuleNoRender.class).getHud().getPotionIcons().should()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderScoreboardSidebar", at = @At("HEAD"), cancellable = true)
    public void noRender_renderScoreboardSidebar(MatrixStack matrices, ScoreboardObjective objective, CallbackInfo ci) {
        if (TarasandeMain.Companion.managerModule().get(ModuleNoRender.class).getHud().getScoreboard().should()) {
            ci.cancel();
        }
    }
}
