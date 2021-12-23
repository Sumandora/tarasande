package su.mandora.tarasande.mixin.mixins.multiconnect;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin(targets = "net.earthcomputer.multiconnect.connect.ConnectionHandler", remap = false)
public class MixinConnectionHandler {

    @SuppressWarnings("UnresolvedMixinReference")
    @Redirect(method = "preConnect", at = @At(value = "INVOKE", target = "Lnet/earthcomputer/multiconnect/connect/ConnectionHandler;normalizeAddress(Ljava/lang/String;)Ljava/lang/String;"))
    private static String hookedNormalizeAddress(String addressStr) {
        return ""; // bypass to any server restrictions
    }

}
