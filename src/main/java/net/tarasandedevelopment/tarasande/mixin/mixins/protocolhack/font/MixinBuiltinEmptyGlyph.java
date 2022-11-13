package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.font;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viaprotocolhack.util.VersionList;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.font.BuiltinEmptyGlyph;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BuiltinEmptyGlyph.class)
public class MixinBuiltinEmptyGlyph {

    @Inject(method = "getAdvance", at = @At("HEAD"), cancellable = true)
    public void resetAdvance(CallbackInfoReturnable<Float> cir) {
        try {
            if (VersionList.isOlderOrEqualTo(ProtocolVersion.v1_12_2) && !FabricLoader.getInstance().isModLoaded("dashloader")) {
                cir.setReturnValue(0F);
            }
        } catch (Exception ignored) {
        }
    }
}
