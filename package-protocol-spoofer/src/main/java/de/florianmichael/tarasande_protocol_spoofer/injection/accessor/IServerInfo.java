package de.florianmichael.tarasande_protocol_spoofer.injection.accessor;

import de.florianmichael.tarasande_protocol_spoofer.spoofer.forgefaker.payload.IForgePayload;

public interface IServerInfo {

    IForgePayload tarasande_getForgePayload();

    void tarasande_setForgePayload(final IForgePayload forgePayload);

}
