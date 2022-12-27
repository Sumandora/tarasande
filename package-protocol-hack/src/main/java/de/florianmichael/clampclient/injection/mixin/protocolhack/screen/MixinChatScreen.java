/**
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.2--
 *
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license. The creator assumes no responsibility for any infringements
 * that have arisen, are arising or will arise from this project / file. If this licence is used anywhere,
 * the latest version published by the author Florian Michael (aka EnZaXD) always applies automatically.
 *
 * Changelog:
 *     v1.0:
 *         Added License
 *     v1.1:
 *         Ownership withdrawn
 *     v1.2:
 *         Version-independent validity and automatic renewal
 */

package de.florianmichael.clampclient.injection.mixin.protocolhack.screen;

import de.florianmichael.clampclient.injection.mixininterface.IChatInputSuggestor_Protocol;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.tarasandedevelopment.tarasande_protocol_hack.util.values.ProtocolHackValues;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public class MixinChatScreen extends Screen {

    @Shadow
    ChatInputSuggestor chatInputSuggestor;

    protected MixinChatScreen(Text title) {
        super(title);
    }

    @Inject(method = "keyPressed", at = @At("HEAD"))
    public void reAddKeyBind(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (!ProtocolHackValues.INSTANCE.getRemoveNewTabCompletion().getValue()) {
            return;
        }

        if (keyCode == MinecraftClient.getInstance().options.playerListKey.boundKey.getCode() && ((IChatInputSuggestor_Protocol) this.chatInputSuggestor).protcolhack_getPendingSuggestionSize() == 0) {
            this.chatInputSuggestor.setWindowActive(true);
            ((IChatInputSuggestor_Protocol) this.chatInputSuggestor).protocolhack_setNativeCompletion(false);
            this.chatInputSuggestor.refresh();
        }
    }

    @Inject(method = "onChatFieldUpdate", at = @At(value = "HEAD"))
    public void removePermanentRefreshing(String chatText, CallbackInfo ci) {
        ((IChatInputSuggestor_Protocol) this.chatInputSuggestor).protocolhack_setNativeCompletion(ProtocolHackValues.INSTANCE.getRemoveNewTabCompletion().getValue());
    }
}
