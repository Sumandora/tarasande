package net.tarasandedevelopment.tarasande.injection.mixin.feature.clientvalue;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.InGameHud;
import net.tarasandedevelopment.tarasande.feature.clientvalue.impl.debug.Chat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(InGameHud.class)
public class MixinInGameHud {

    @Redirect(method = "clear", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;clear(Z)V"))
    public void removeChatClear(ChatHud instance, boolean clearHistory) {
        if (!Chat.INSTANCE.getDontResetHistoryOnDisconnect().getValue()) {
            instance.clear(clearHistory);
        }
    }
}
