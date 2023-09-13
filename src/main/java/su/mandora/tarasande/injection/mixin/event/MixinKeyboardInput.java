package su.mandora.tarasande.injection.mixin.event;

import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.event.EventDispatcher;
import su.mandora.tarasande.event.impl.EventInput;

@Mixin(KeyboardInput.class)
public class MixinKeyboardInput extends Input {

    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/input/KeyboardInput;sneaking:Z", shift = At.Shift.AFTER), cancellable = true)
    public void hookEventInput(boolean slowDown, float f, CallbackInfo ci) {
        EventInput eventInput = new EventInput(this, slowDown, f);
        EventDispatcher.INSTANCE.call(eventInput);

        if (!eventInput.getCancelled()) {
            if (slowDown && !eventInput.getSlowDown())
                ci.cancel(); // This is awful, but I can't change the parameter, because Java doesn't support references
        }
    }
}
