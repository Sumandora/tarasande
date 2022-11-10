package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.font;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.GlyphRenderer;
import net.minecraft.client.font.RenderableGlyph;
import net.tarasandedevelopment.tarasande.features.protocol.platform.ProtocolHackValues;
import net.tarasandedevelopment.tarasande.mixin.accessor.protocolhack.IFontStorage_Protocol;
import net.tarasandedevelopment.tarasande.util.protocol.BuiltinEmptyGlyph1_12_2;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(FontStorage.class)
public abstract class MixinFontStorage implements IFontStorage_Protocol {

    @Shadow protected abstract GlyphRenderer getGlyphRenderer(RenderableGlyph c);

    @Shadow @Final private Int2ObjectMap<GlyphRenderer> glyphRendererCache;
    @Shadow @Final private Int2ObjectMap<Object> glyphCache;
    @Shadow @Final private Int2ObjectMap<IntList> charactersByWidth;
    @Unique
    private GlyphRenderer unknownGlyphRenderer;

    @Inject(method = "setFonts", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/BuiltinEmptyGlyph;bake(Ljava/util/function/Function;)Lnet/minecraft/client/font/GlyphRenderer;"))
    public void resetFontRenderer(List<Font> fonts, CallbackInfo ci) {
        this.unknownGlyphRenderer = BuiltinEmptyGlyph1_12_2.VERY_MISSING.bake(this::getGlyphRenderer);
    }

    @Inject(method = "getRectangleRenderer", at = @At("HEAD"), cancellable = true)
    public void setCustomRenderer(CallbackInfoReturnable<GlyphRenderer> cir) {
        if (ProtocolHackValues.INSTANCE.getFontCacheFix().getValue() && ProtocolHackValues.INSTANCE.getFontCacheFix().isEnabled()) {
            cir.setReturnValue(this.unknownGlyphRenderer);
        }
    }

    @Override
    public void protocolhack_clearCaches() {
        this.glyphRendererCache.clear();
        this.glyphCache.clear();
        this.charactersByWidth.clear();
    }
}
