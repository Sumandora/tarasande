package de.florianmichael.clampclient.injection.mixininterface;

import com.viaversion.viaversion.api.minecraft.PlayerMessageSignature;

public interface IReceivedMessagesStorage_Protocol {

    PlayerMessageSignature protocolhack_getLastSignature();

}
