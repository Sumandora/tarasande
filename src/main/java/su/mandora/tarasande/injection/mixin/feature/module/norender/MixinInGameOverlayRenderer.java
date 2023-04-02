package su.mandora.tarasande.injection.mixin.feature.module.norender;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule;
import su.mandora.tarasande.system.feature.modulesystem.impl.render.ModuleNoRender;

@Mixin(InGameOverlayRenderer.class)
public class MixinInGameOverlayRenderer {

    @Inject(method = "renderFireOverlay", at = @At("HEAD"), cancellable = true)
    private static void noRender_renderFireOverlay(MinecraftClient client, MatrixStack matrices, CallbackInfo ci) {
        if (ManagerModule.INSTANCE.get(ModuleNoRender.class).getOverlay().getFireOverlay().should()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderUnderwaterOverlay", at = @At("HEAD"), cancellable = true)
    private static void noRender_renderUnderwaterOverlay(MinecraftClient client, MatrixStack matrices, CallbackInfo ci) {
        if (ManagerModule.INSTANCE.get(ModuleNoRender.class).getOverlay().getWaterOverlay().should()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderInWallOverlay", at = @At("HEAD"), cancellable = true)
    private static void noRender_renderInWallOverlay(Sprite sprite, MatrixStack matrices, CallbackInfo ci) {
        if (ManagerModule.INSTANCE.get(ModuleNoRender.class).getOverlay().getInWallOverlay().should()) {
            ci.cancel();
        }
    }
}
