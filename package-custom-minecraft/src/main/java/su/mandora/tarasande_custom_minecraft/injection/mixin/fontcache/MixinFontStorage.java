package su.mandora.tarasande_custom_minecraft.injection.mixin.fontcache;

import su.mandora.tarasande_custom_minecraft.BuiltinEmptyGlyph1_12_2;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.GlyphRenderer;
import net.minecraft.client.font.RenderableGlyph;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(FontStorage.class)
public abstract class MixinFontStorage {

    @Shadow protected abstract GlyphRenderer getGlyphRenderer(RenderableGlyph c);

    @Unique
    private GlyphRenderer unknownGlyphRenderer;

    @Inject(method = "setFonts", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/BuiltinEmptyGlyph;bake(Ljava/util/function/Function;)Lnet/minecraft/client/font/GlyphRenderer;", ordinal = 0, shift = At.Shift.AFTER))
    public void injectSetFonts(List<Font> fonts, CallbackInfo ci) {
        this.unknownGlyphRenderer = BuiltinEmptyGlyph1_12_2.VERY_MISSING.bake(this::getGlyphRenderer);
    }

    @Inject(method = "getRectangleRenderer", at = @At("HEAD"), cancellable = true)
    public void setCustomRenderer(CallbackInfoReturnable<GlyphRenderer> cir) {
        cir.setReturnValue(this.unknownGlyphRenderer);
    }
}
