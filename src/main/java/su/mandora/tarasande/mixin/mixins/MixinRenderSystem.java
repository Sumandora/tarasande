package su.mandora.tarasande.mixin.mixins;

import com.mojang.blaze3d.systems.RenderSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.util.math.rotation.RotationUtil;

@Mixin(RenderSystem.class)
public class MixinRenderSystem {

    @Inject(method = "flipFrame", at = @At("HEAD"), remap = false)
    private static void injectFlipFrame(long window, CallbackInfo ci) {
        RotationUtil.INSTANCE.updateFakeRotation();
    }
}
