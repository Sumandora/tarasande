package su.mandora.tarasande_protocol_spoofer.injection.accessor;

import su.mandora.tarasande_protocol_spoofer.tarasandevalues.forge.payload.IForgePayload;
import su.mandora.tarasande_protocol_spoofer.tarasandevalues.forge.payload.IForgePayload;

public interface IServerInfo {

    IForgePayload tarasande_getForgePayload();

    void tarasande_setForgePayload(final IForgePayload forgePayload);

}
