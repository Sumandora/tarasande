package net.tarasandedevelopment.tarasande.injection.mixin.feature.module.norender;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ManagerModule;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.render.ModuleNoRender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {

    @Inject(method = "bobView", at = @At("HEAD"), cancellable = true)
    public void noRender_bobView(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        if (ManagerModule.INSTANCE.get(ModuleNoRender.class).getOverlay().getHurtCam().should()) {
            ci.cancel();
        }
    }

    @Redirect(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;lerp(FFF)F"))
    public float noRender_renderWorld(float delta, float start, float end) {
        if (ManagerModule.INSTANCE.get(ModuleNoRender.class).getOverlay().getNoNausea().should()) {
            return 0;
        }
        return MathHelper.lerp(delta, start, end);
    }

    @Inject(method = "showFloatingItem", at = @At("HEAD"), cancellable = true)
    public void noRender_showFloatingItem(ItemStack floatingItem, CallbackInfo ci) {
        if (ManagerModule.INSTANCE.get(ModuleNoRender.class).getOverlay().getTotemAnimation().should()) {
            ci.cancel();
        }
    }
}
