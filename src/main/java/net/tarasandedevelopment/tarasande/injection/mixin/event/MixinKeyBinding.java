package net.tarasandedevelopment.tarasande.injection.mixin.event;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.MutableText;
import net.tarasandedevelopment.tarasande.event.EventKeyBindingIsPressed;
import net.tarasandedevelopment.tarasande.util.player.chat.CustomChat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.mandora.event.EventDispatcher;

@Mixin(KeyBinding.class)
public abstract class MixinKeyBinding {

    @Shadow
    public int timesPressed;

    @Shadow public abstract String getTranslationKey();

    @Unique
    private boolean tarasande_wasPressed = false;

    @Inject(method = "isPressed", at = @At("RETURN"), cancellable = true)
    public void hookEventKeyBindingIsPressed(CallbackInfoReturnable<Boolean> cir) {
        EventKeyBindingIsPressed eventKeyBindingIsPressed = new EventKeyBindingIsPressed((KeyBinding) (Object) this, cir.getReturnValue());
        EventDispatcher.INSTANCE.call(eventKeyBindingIsPressed);
        if(eventKeyBindingIsPressed.getDirty() && !tarasande_wasPressed && !cir.getReturnValue() && eventKeyBindingIsPressed.getPressed()) {
            timesPressed++;
            CustomChat.INSTANCE.printChatMessage(MutableText.of(new LiteralTextContent("LOLOLOLOL " + this.getTranslationKey())));
        }
        cir.setReturnValue(tarasande_wasPressed = eventKeyBindingIsPressed.getPressed());
    }
}
