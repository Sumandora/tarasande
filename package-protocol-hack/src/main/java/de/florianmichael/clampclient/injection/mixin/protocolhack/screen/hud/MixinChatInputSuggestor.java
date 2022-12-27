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

package de.florianmichael.clampclient.injection.mixin.protocolhack.screen.hud;

import de.florianmichael.clampclient.injection.mixininterface.IChatInputSuggestor_Protocol;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.tarasandedevelopment.tarasande_protocol_hack.util.values.ProtocolHackValues;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatInputSuggestor.class)
public abstract class MixinChatInputSuggestor implements IChatInputSuggestor_Protocol {

    @Shadow
    @Nullable
    private ChatInputSuggestor.@Nullable SuggestionWindow window;

    @Shadow public abstract void clearWindow();

    @Unique
    private boolean protocolhack_isCustomCompletion = false;

    @Inject(method = "refresh", at = @At("HEAD"), cancellable = true)
    public void injectRefresh(CallbackInfo ci) {
        if (this.protocolhack_isCustomCompletion) {
            ci.cancel();
        }
    }

    @Inject(method = "keyPressed", at = @At("RETURN"))
    public void autoClear(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (!ProtocolHackValues.INSTANCE.getRemoveNewTabCompletion().getValue()) {
            return;
        }

        if(!cir.getReturnValue())
            clearWindow();
    }

    @Override
    public void protocolhack_setNativeCompletion(boolean nativeCompletion) {
        this.protocolhack_isCustomCompletion = nativeCompletion;
    }

    @Override
    public int protcolhack_getPendingSuggestionSize() {
        if (this.window == null) return 0;

        return this.window.suggestions.size();
    }
}
