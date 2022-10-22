package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.chat;

import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.tarasandedevelopment.tarasande.mixin.accessor.protocolhack.IChatInputSuggestor_Protocol;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatInputSuggestor.class)
public class MixinChatInputSuggestor implements IChatInputSuggestor_Protocol {

    @Shadow @Nullable private ChatInputSuggestor.@Nullable SuggestionWindow window;
    @Unique
    private boolean isCustomCompletion = false;

    @Inject(method = "refresh", at = @At("HEAD"), cancellable = true)
    public void injectRefresh(CallbackInfo ci) {
        if (this.isCustomCompletion) {
            ci.cancel();
        }
    }

    @Override
    public void tarasande_setNativeCompletion(boolean nativeCompletion) {
        this.isCustomCompletion = nativeCompletion;
    }

    @Override
    public int tarasande_getPendingSuggestionSize() {
        if (this.window == null) return 0;

        return this.window.suggestions.size();
    }
}
