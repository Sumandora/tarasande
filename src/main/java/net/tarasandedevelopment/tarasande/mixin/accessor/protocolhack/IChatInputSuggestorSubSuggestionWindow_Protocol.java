package net.tarasandedevelopment.tarasande.mixin.accessor.protocolhack;

import com.mojang.brigadier.suggestion.Suggestion;

import java.util.List;

public interface IChatInputSuggestorSubSuggestionWindow_Protocol {

    List<Suggestion> tarasande_getSuggestions();
}
