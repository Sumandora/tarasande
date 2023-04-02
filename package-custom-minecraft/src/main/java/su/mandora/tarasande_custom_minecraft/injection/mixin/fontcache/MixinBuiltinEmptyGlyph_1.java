package su.mandora.tarasande_custom_minecraft.injection.mixin.fontcache;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net/minecraft/client/font/BuiltinEmptyGlyph$1")
public class MixinBuiltinEmptyGlyph_1 {

    @Inject(method = {"getWidth", "getHeight"}, at = @At("HEAD"), cancellable = true)
    public void resetDimension(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(0);
    }
}
