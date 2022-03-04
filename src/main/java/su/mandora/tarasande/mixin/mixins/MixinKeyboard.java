package su.mandora.tarasande.mixin.mixins;

import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.TarasandeMain;
import su.mandora.tarasande.event.EventKey;

@Mixin(Keyboard.class)
public class MixinKeyboard {

    @Inject(method = "onKey", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;", shift = At.Shift.BEFORE), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;toggleShadersEnabled()V"), to = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;onKeyPressed(Lnet/minecraft/client/util/InputUtil$Key;)V")), cancellable = true)
    public void injectOnKeyPress(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        EventKey eventKey = new EventKey(key);
        TarasandeMain.Companion.get().getManagerEvent().call(eventKey);
        if (eventKey.getCancelled())
            ci.cancel();
    }

}
