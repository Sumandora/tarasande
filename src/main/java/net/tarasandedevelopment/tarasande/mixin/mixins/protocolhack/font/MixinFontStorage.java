package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.font;

import com.mojang.blaze3d.systems.RenderSystem;
import de.florianmichael.viaprotocolhack.util.VersionList;
import net.minecraft.client.font.*;
import net.tarasandedevelopment.tarasande.mixin.accessor.protocolhack.IFontStorage_Protocol;
import net.tarasandedevelopment.tarasande.protocol.font.BuiltinEmptyGlyph_1_12;
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

    @Shadow public abstract void setFonts(List<Font> fonts);

    @Shadow @Final private List<Font> fonts;
    @Unique
    private GlyphRenderer unknownGlyphRenderer;

    @Unique
    private List<Font> myFonts;

    @Unique
    private boolean ownReloading;

    @Inject(method = "setFonts", at = @At("HEAD"))
    public void saveFonts(List<Font> fonts, CallbackInfo ci) {
        if (ownReloading) return;

        this.myFonts = fonts;
    }

    @Inject(method = "setFonts", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/BuiltinEmptyGlyph;bake(Ljava/util/function/Function;)Lnet/minecraft/client/font/GlyphRenderer;", ordinal = 0, shift = At.Shift.AFTER))
    public void injectSetFonts(List<Font> fonts, CallbackInfo ci) {
        this.unknownGlyphRenderer = BuiltinEmptyGlyph_1_12.VERY_MISSING.bake(this::getGlyphRenderer);
    }

    @Inject(method = "getRectangleRenderer", at = @At("HEAD"), cancellable = true)
    public void replaceRenderer(CallbackInfoReturnable<GlyphRenderer> cir) {
        if (VersionList.isOlderOrEqualTo(VersionList.R1_12_2))
            cir.setReturnValue(this.unknownGlyphRenderer);
    }

    @Override
    public void reload() {
        ownReloading = true;
        if (this.fonts != null) {
            this.fonts.clear();
            RenderSystem.recordRenderCall(() -> setFonts(myFonts));
        }
        ownReloading = false;
    }
}
