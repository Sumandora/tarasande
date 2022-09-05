package su.mandora.tarasande.mixin.mixins;

import net.minecraft.client.gl.GlDebug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GlDebug.class)
public class MixinGlDebug {

    @Inject(method = "info", at = @At("HEAD"))
    private static void injectInfo(int source, int type, int id, int severity, int messageLength, long message, long l, CallbackInfo ci) {
        new IllegalStateException().printStackTrace();
    }

}
