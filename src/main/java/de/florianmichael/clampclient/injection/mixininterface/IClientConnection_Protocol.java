package de.florianmichael.clampclient.injection.mixininterface;

import com.viaversion.viaversion.api.connection.UserConnection;

public interface IClientConnection_Protocol {

    void protocolhack_setViaConnection(final UserConnection userConnection);

    UserConnection protocolhack_getViaConnection();
}
