package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.font;

import net.tarasandedevelopment.tarasande.features.protocol.platform.ProtocolHackValues;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net/minecraft/client/font/BuiltinEmptyGlyph$1")
public class MixinBuiltinEmptyGlyphSubMissing {

    @Inject(method = { "getWidth", "getHeight" }, at = @At("HEAD"), cancellable = true)
    public void resetDimension(CallbackInfoReturnable<Integer> cir) {
        try {
            if (ProtocolHackValues.INSTANCE.getFontCacheFix().getValue() && ProtocolHackValues.INSTANCE.getFontCacheFix().isEnabled()) {
                cir.setReturnValue(0);
            }
        } catch (Exception ignored) {
        }
    }
}
