package net.tarasandedevelopment.tarasande.mixin.mixins.module.norender;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.module.render.ModuleNoRender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {

    @Inject(method = "bobViewWhenHurt", at = @At("HEAD"), cancellable = true)
    public void noRender_bobViewWhenHurt(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        if (TarasandeMain.Companion.get().getManagerModule().get(ModuleNoRender.class).getOverlay().getHurtCam().should()) {
            ci.cancel();
        }
    }

    @Redirect(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;lerp(FFF)F"))
    public float noRender_renderWorld(float delta, float start, float end) {
        if (TarasandeMain.Companion.get().getManagerModule().get(ModuleNoRender.class).getOverlay().getNoNausea().should()) {
            return 0;
        }
        return MathHelper.lerp(delta, start, end);
    }

    @Inject(method = "showFloatingItem", at = @At("HEAD"), cancellable = true)
    public void noRender_showFloatingItem(ItemStack floatingItem, CallbackInfo ci) {
        if (TarasandeMain.Companion.get().getManagerModule().get(ModuleNoRender.class).getOverlay().getTotemAnimation().should()) {
            ci.cancel();
        }
    }
}
