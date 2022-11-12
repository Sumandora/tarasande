package net.tarasandedevelopment.tarasande.mixin.mixins.event;

import net.minecraft.client.Keyboard;
import net.tarasandedevelopment.events.EventDispatcher;
import net.tarasandedevelopment.events.impl.EventKey;
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
