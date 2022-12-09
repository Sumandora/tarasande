package net.tarasandedevelopment.tarasande.injection.mixin.event.screen;

import net.minecraft.client.gui.screen.ChatScreen;
import net.tarasandedevelopment.tarasande.event.EventChat;
import net.tarasandedevelopment.tarasande.injection.accessor.IChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.mandora.event.EventDispatcher;

@Mixin(ChatScreen.class)
public class MixinChatScreen implements IChatScreen {

    @Unique
    private boolean tarasande_bypassChat;

    @Inject(method = "sendMessage", at = @At("HEAD"), cancellable = true)
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
