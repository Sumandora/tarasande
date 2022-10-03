package net.tarasandedevelopment.tarasande.mixin.mixins;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.event.EventKeyBindingIsPressed;
import net.tarasandedevelopment.tarasande.mixin.accessor.IKeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyBinding.class)
public class MixinKeyBinding implements IKeyBinding {
    @Shadow
    private int timesPressed;

    @Shadow
    private InputUtil.Key boundKey;

    @Shadow
    private boolean pressed;

    @Inject(method = "isPressed", at = @At("TAIL"), cancellable = true)
    public void injectIsPressed(CallbackInfoReturnable<Boolean> cir) {
        EventKeyBindingIsPressed eventKeyBindingIsPressed = new EventKeyBindingIsPressed((KeyBinding) (Object) this, cir.getReturnValue());
        TarasandeMain.Companion.get().getManagerEvent().call(eventKeyBindingIsPressed);
        cir.setReturnValue(eventKeyBindingIsPressed.getPressed());
    }

    @Override
    public void tarasande_setTimesPressed(int timesPressed) {
        this.timesPressed = timesPressed;
    }

    @Override
    public boolean tarasande_forceIsPressed() {
        return pressed;
    }

    @Override
    public InputUtil.Key tarasande_getBoundKey() {
        return boundKey;
    }

    @Override
    public void tarasande_increaseTimesPressed() {
        timesPressed++;
    }
}
