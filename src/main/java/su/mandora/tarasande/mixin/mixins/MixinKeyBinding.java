package su.mandora.tarasande.mixin.mixins;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.mandora.tarasande.TarasandeMain;
import su.mandora.tarasande.event.EventKeyBindingIsPressed;
import su.mandora.tarasande.mixin.accessor.IKeyBinding;

@Mixin(KeyBinding.class)
public class MixinKeyBinding implements IKeyBinding {
    @Shadow
    private int timesPressed;

    @Shadow
    private InputUtil.Key boundKey;

    @Shadow
    private boolean pressed;

    @Inject(method = "isPressed", at = @At("RETURN"), cancellable = true)
    public void injectIsPressed(CallbackInfoReturnable<Boolean> cir) {
        EventKeyBindingIsPressed eventKeyBindingIsPressed = new EventKeyBindingIsPressed((KeyBinding) (Object) this, cir.getReturnValue());
        TarasandeMain.Companion.get().getManagerEvent().call(eventKeyBindingIsPressed);
        cir.setReturnValue(eventKeyBindingIsPressed.getPressed());
    }

    @Override
    public void setTimesPressed(int timesPressed) {
        this.timesPressed = timesPressed;
    }

    @Override
    public boolean forceIsPressed() {
        return pressed;
    }

    @Override
    public InputUtil.Key getBoundKey() {
        return boundKey;
    }
}
