package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.font;

import com.mojang.blaze3d.systems.RenderSystem;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viaprotocolhack.util.VersionList;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.GlyphRenderer;
import net.minecraft.client.font.RenderableGlyph;
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

    @Shadow @Final private List<Font> fonts;

    @Shadow public abstract void setFonts(List<Font> fonts);

    @Unique
    private GlyphRenderer unknownGlyphRenderer;

    @Unique
    private List<Font> myFonts;

    @Unique
    private boolean bypassReload;

    @Inject(method = "setFonts", at = @At("HEAD"))
    public void saveFonts(List<Font> fonts, CallbackInfo ci) {
        if (bypassReload) {
            bypassReload = false;
            return;
        }
        this.myFonts = fonts;
    }

    @Inject(method = "setFonts", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/BuiltinEmptyGlyph;bake(Ljava/util/function/Function;)Lnet/minecraft/client/font/GlyphRenderer;", ordinal = 0, shift = At.Shift.AFTER))
    public void injectSetFonts(List<Font> fonts, CallbackInfo ci) {
        this.unknownGlyphRenderer = BuiltinEmptyGlyph1_12_2.VERY_MISSING.bake(this::getGlyphRenderer);
    }

    @Inject(method = "getRectangleRenderer", at = @At("HEAD"), cancellable = true)
    public void setCustomRenderer(CallbackInfoReturnable<GlyphRenderer> cir) {
        if (VersionList.isOlderOrEqualTo(ProtocolVersion.v1_12_2) && !FabricLoader.getInstance().isModLoaded("dashloader")) {
            cir.setReturnValue(this.unknownGlyphRenderer);
        }
    }

    @Override
    public void protocolhack_clearCaches() {
        bypassReload = true;

        if (this.fonts != null) {
            this.fonts.clear();

            if (this.myFonts != null) {
                RenderSystem.recordRenderCall(() -> setFonts(myFonts));
            }
        }
    }
}
