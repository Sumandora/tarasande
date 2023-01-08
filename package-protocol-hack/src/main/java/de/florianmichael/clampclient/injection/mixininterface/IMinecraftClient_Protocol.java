package de.florianmichael.clampclient.injection.mixininterface;

public interface IMinecraftClient_Protocol {

    void protocolhack_trackKeyboardInteraction(final Runnable interaction);
    void protocolhack_trackMouseInteraction(final Runnable interaction);
}
