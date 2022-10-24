package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack;

import de.florianmichael.viaprotocolhack.util.VersionList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.screen.ChatScreen;
import net.tarasandedevelopment.tarasande.mixin.accessor.protocolhack.IChatInputSuggestor_Protocol;
import net.tarasandedevelopment.tarasande.protocol.platform.ProtocolHackValues;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public class MixinChatScreen {

    @Shadow
    ChatInputSuggestor chatInputSuggestor;

    @Inject(method = "keyPressed", at = @At("HEAD"))
    public void reAddKeyBind(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (VersionList.isNewerTo(VersionList.R1_13) || !ProtocolHackValues.INSTANCE.getRemoveNewTabCompletion().getValue())
            return;

        if (keyCode == MinecraftClient.getInstance().options.playerListKey.boundKey.getCode() && ((IChatInputSuggestor_Protocol) this.chatInputSuggestor).protcolhack_getPendingSuggestionSize() == 0) {
            this.chatInputSuggestor.setWindowActive(true);
            ((IChatInputSuggestor_Protocol) this.chatInputSuggestor).protocolhack_setNativeCompletion(false);
            this.chatInputSuggestor.refresh();
        }
    }

    @Inject(method = "onChatFieldUpdate", at = @At(value = "HEAD"))
    public void removePermanentRefreshing(String chatText, CallbackInfo ci) {
        ((IChatInputSuggestor_Protocol) this.chatInputSuggestor).protocolhack_setNativeCompletion(VersionList.isOlderTo(VersionList.R1_13) && ProtocolHackValues.INSTANCE.getRemoveNewTabCompletion().getValue());
    }
}