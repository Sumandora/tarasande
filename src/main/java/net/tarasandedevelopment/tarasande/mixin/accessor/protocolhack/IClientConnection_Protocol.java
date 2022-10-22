package net.tarasandedevelopment.tarasande.mixin.accessor.protocolhack;

import com.viaversion.viaversion.api.connection.UserConnection;

public interface IClientConnection_Protocol {

    void protocolhack_setViaConnection(final UserConnection userConnection);

    UserConnection protocolhack_getViaConnection();
}
