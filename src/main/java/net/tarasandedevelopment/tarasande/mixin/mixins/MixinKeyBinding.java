package net.tarasandedevelopment.tarasande.mixin.mixins;

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
    public void injectIsPressed(CallbackInfoReturnable<Boolean> cir) {
        final EventKeyBindingIsPressed eventKeyBindingIsPressed = new EventKeyBindingIsPressed((KeyBinding) (Object) this, cir.getReturnValue());

        TarasandeMain.Companion.get().getEventDispatcher().call(eventKeyBindingIsPressed);
        cir.setReturnValue(eventKeyBindingIsPressed.getPressed());
    }
}
