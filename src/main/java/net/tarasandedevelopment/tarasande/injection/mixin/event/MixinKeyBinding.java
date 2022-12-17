package net.tarasandedevelopment.tarasande.injection.mixin.event;

import net.minecraft.client.option.KeyBinding;
import net.tarasandedevelopment.tarasande.event.EventKeyBindingIsPressed;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.mandora.event.EventDispatcher;

@Mixin(KeyBinding.class)
public class MixinKeyBinding {

    @Shadow
    public int timesPressed;

    @Inject(method = "isPressed", at = @At("RETURN"), cancellable = true)
    public void hookEventKeyBindingIsPressed(CallbackInfoReturnable<Boolean> cir) {
        EventKeyBindingIsPressed eventKeyBindingIsPressed = new EventKeyBindingIsPressed((KeyBinding) (Object) this, cir.getReturnValue());
        EventDispatcher.INSTANCE.call(eventKeyBindingIsPressed);
        if(eventKeyBindingIsPressed.getDirty() && eventKeyBindingIsPressed.getPressed()) {
            timesPressed++;
        }
        cir.setReturnValue(eventKeyBindingIsPressed.getPressed());
    }
}
