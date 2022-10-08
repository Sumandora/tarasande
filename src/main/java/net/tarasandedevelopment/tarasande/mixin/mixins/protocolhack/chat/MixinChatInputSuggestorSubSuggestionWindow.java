package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.chat;

import com.mojang.brigadier.suggestion.Suggestion;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.tarasandedevelopment.tarasande.mixin.accessor.protocolhack.IChatInputSuggestorSubSuggestionWindow_Protocol;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(ChatInputSuggestor.SuggestionWindow.class)
public class MixinChatInputSuggestorSubSuggestionWindow implements IChatInputSuggestorSubSuggestionWindow_Protocol {

    @Shadow @Final private List<Suggestion> suggestions;

    @Override
    public List<Suggestion> tarasande_getSuggestions() {
        return this.suggestions;
    }
}
