package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.screen.hud;

import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.tarasandedevelopment.tarasande.mixin.accessor.protocolhack.IChatInputSuggestor_Protocol;
import net.tarasandedevelopment.tarasande.protocolhack.platform.ProtocolHackValues;
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
        if (!ProtocolHackValues.INSTANCE.getRemoveNewTabCompletion().getValue())
            return;

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
