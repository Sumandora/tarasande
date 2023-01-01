package de.florianmichael.clampclient.injection.mixininterface;

import java.util.concurrent.ConcurrentLinkedDeque;

public interface IMinecraftClient_Protocol {

    ConcurrentLinkedDeque<Runnable> protocolhack_getKeyboardInteractions();
    ConcurrentLinkedDeque<Runnable> protocolhack_getMouseInteractions();
}
