package net.tarasandedevelopment.tarasande.mixin.mixins.event;

import net.minecraft.client.option.KeyBinding;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.event.EventKeyBindingIsPressed;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyBinding.class)
public class MixinKeyBinding {

    @Inject(method = "isPressed", at = @At("RETURN"), cancellable = true)
    public void hookEventKeyBindingIsPressed(CallbackInfoReturnable<Boolean> cir) {
        EventKeyBindingIsPressed eventKeyBindingIsPressed = new EventKeyBindingIsPressed((KeyBinding) (Object) this, cir.getReturnValue());
        TarasandeMain.Companion.get().getManagerEvent().call(eventKeyBindingIsPressed);
        cir.setReturnValue(eventKeyBindingIsPressed.getPressed());
    }
}
