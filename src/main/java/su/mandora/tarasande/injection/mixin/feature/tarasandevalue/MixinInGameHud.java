package su.mandora.tarasande.injection.mixin.feature.tarasandevalue;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.InGameHud;
import su.mandora.tarasande.feature.tarasandevalue.impl.debug.Chat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import su.mandora.tarasande.feature.tarasandevalue.impl.debug.Chat;

@Mixin(InGameHud.class)
public class MixinInGameHud {

    @Redirect(method = "clear", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;clear(Z)V"))
    public void removeChatClear(ChatHud instance, boolean clearHistory) {
        if (!Chat.INSTANCE.getDontResetHistoryOnDisconnect().getValue()) {
            instance.clear(clearHistory);
        }
    }
}
