package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.font;

import net.minecraft.client.font.BuiltinEmptyGlyph;
import net.tarasandedevelopment.tarasande.features.protocol.platform.ProtocolHackValues;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BuiltinEmptyGlyph.class)
public class MixinBuiltinEmptyGlyph {

    @Inject(method = "getAdvance", at = @At("HEAD"), cancellable = true)
    public void resetAdvance(CallbackInfoReturnable<Float> cir) {
        try {
            if (ProtocolHackValues.INSTANCE.getFontCacheFix().getValue() && ProtocolHackValues.INSTANCE.getFontCacheFix().isEnabled()) {
                cir.setReturnValue(0F);
            }
        } catch (Exception ignored) {
        }
    }
}
