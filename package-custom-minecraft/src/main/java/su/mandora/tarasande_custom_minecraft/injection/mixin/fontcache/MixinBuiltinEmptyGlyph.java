package su.mandora.tarasande_custom_minecraft.injection.mixin.fontcache;

import net.minecraft.client.font.BuiltinEmptyGlyph;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BuiltinEmptyGlyph.class)
public class MixinBuiltinEmptyGlyph {

    @Inject(method = "getAdvance", at = @At("HEAD"), cancellable = true)
    public void resetAdvance(CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(0F);
    }
}
