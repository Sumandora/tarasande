package de.florianmichael.clampclient.injection.mixininterface;

public interface IChatInputSuggestor_Protocol {

    void protocolhack_setNativeCompletion(boolean nativeCompletion);

    int protcolhack_getPendingSuggestionSize();
}
