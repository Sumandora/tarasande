package su.mandora.tarasande_protocol_spoofer.injection.accessor;

import su.mandora.tarasande_protocol_spoofer.tarasandevalues.forge.payload.IForgePayload;

public interface IServerMetadata {

    IForgePayload tarasande_getForgePayload();

    void tarasande_setForgePayload(IForgePayload forgePayload);

}