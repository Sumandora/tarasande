package su.mandora.tarasande.injection.mixin.event;

import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.mandora.tarasande.event.EventDispatcher;
import su.mandora.tarasande.event.impl.EventKeyBindingIsPressed;

@Mixin(KeyBinding.class)
public abstract class MixinKeyBinding {

    @Shadow
    public int timesPressed;
    @Unique
    private boolean tarasande_wasPressed = true;

    @Shadow
    public abstract String getTranslationKey();

    @Inject(method = "isPressed", at = @At("RETURN"), cancellable = true)
    public void hookEventKeyBindingIsPressed(CallbackInfoReturnable<Boolean> cir) {
        EventKeyBindingIsPressed eventKeyBindingIsPressed = new EventKeyBindingIsPressed((KeyBinding) (Object) this, cir.getReturnValue());
        EventDispatcher.INSTANCE.call(eventKeyBindingIsPressed);
        if (eventKeyBindingIsPressed.getDirty() && !tarasande_wasPressed && !cir.getReturnValue() && eventKeyBindingIsPressed.getPressed()) {
            timesPressed++;
        }
        cir.setReturnValue(tarasande_wasPressed = eventKeyBindingIsPressed.getPressed());
    }
}
