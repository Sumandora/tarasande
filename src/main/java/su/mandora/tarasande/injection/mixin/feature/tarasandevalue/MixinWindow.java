package su.mandora.tarasande.injection.mixin.feature.tarasandevalue;

import net.minecraft.client.util.Window;
import su.mandora.tarasande.feature.tarasandevalue.impl.DebugValues;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.feature.tarasandevalue.impl.DebugValues;

@Mixin(Window.class)
public class MixinWindow {

    @Inject(method = "logGlError", at = @At("HEAD"))
    public void printCallstack(int error, long description, CallbackInfo ci) {
        if (DebugValues.INSTANCE.getOpenGLErrorDebugger().getValue()) {
            new IllegalStateException().printStackTrace();
        }
    }
}
