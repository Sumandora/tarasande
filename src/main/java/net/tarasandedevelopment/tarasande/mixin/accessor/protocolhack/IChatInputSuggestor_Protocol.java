package net.tarasandedevelopment.tarasande.mixin.accessor.protocolhack;

public interface IChatInputSuggestor_Protocol {

    void tarasande_setNativeCompletion(boolean nativeCompletion);
    int tarasande_getPendingSuggestionSize();
}
