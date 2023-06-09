package su.mandora.tarasande.injection.mixin.event.screen;

import net.minecraft.client.gui.screen.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.mandora.tarasande.event.EventDispatcher;
import su.mandora.tarasande.event.impl.EventChat;
import su.mandora.tarasande.injection.accessor.IChatScreen;

@Mixin(ChatScreen.class)
public class MixinChatScreen implements IChatScreen {

    @Unique
    private boolean tarasande_bypassChat;

    @Inject(method = "sendMessage", at = @At(value = "INVOKE", target = "Ljava/lang/String;startsWith(Ljava/lang/String;)Z"), cancellable = true)
    public void hookEventChat(String chatText, boolean addToHistory, CallbackInfoReturnable<Boolean> cir) {
        if (tarasande_bypassChat)
            return;
        EventChat eventChat = new EventChat(chatText);
        EventDispatcher.INSTANCE.call(eventChat);
        if (eventChat.getCancelled())
            cir.setReturnValue(true);
    }

    @Override
    public void tarasande_setBypassChat(boolean bypassChat) {
        this.tarasande_bypassChat = bypassChat;
    }
}
