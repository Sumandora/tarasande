package de.florianmichael.clampclient.injection.mixininterface;

import java.nio.ByteBuffer;

public interface IPublicKeyData_Protocol {

    ByteBuffer protocolhack_get1_19_0Key();

    void protocolhack_set1_19_0Key(final ByteBuffer oldKey);
}
