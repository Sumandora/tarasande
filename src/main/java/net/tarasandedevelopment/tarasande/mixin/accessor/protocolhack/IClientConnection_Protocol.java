package net.tarasandedevelopment.tarasande.mixin.accessor.protocolhack;

import com.viaversion.viaversion.api.connection.UserConnection;

public interface IClientConnection_Protocol {

    void tarasande_setViaConnection(final UserConnection userConnection);
    UserConnection tarasande_getViaConnection();
}
