package net.tarasandedevelopment.tarasande.injection.mixin.event;

import net.minecraft.client.Keyboard;
import su.mandora.event.EventDispatcher;
import net.tarasandedevelopment.tarasande.event.EventKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class MixinKeyboard {

    @Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
    public void hookEventKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        EventKey eventKey = new EventKey(key, action);
        EventDispatcher.INSTANCE.call(eventKey);
        if (eventKey.getCancelled())
            ci.cancel();
    }

}
