package de.florianmichael.tarasande.mixin.accessor.protocolhack;

import com.viaversion.viaversion.api.connection.UserConnection;

public interface IClientConnection_Protocol {

    void florianMichael_setViaConnection(final UserConnection userConnection);
    UserConnection florianMichael_getViaConnection();
}
