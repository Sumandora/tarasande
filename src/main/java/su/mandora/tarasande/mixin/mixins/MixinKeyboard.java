package su.mandora.tarasande.mixin.mixins;

import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.TarasandeMain;
import su.mandora.tarasande.event.EventKey;

@Mixin(Keyboard.class)
public class MixinKeyboard {

    @Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
    public void injectOnKeyPress(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        EventKey eventKey = new EventKey(key, action);
        TarasandeMain.Companion.get().getManagerEvent().call(eventKey);
        if (eventKey.getCancelled())
            ci.cancel();
    }

}
