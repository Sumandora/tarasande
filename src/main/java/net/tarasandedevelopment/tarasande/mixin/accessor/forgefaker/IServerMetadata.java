package net.tarasandedevelopment.tarasande.mixin.accessor.forgefaker;

import net.tarasandedevelopment.tarasande.system.feature.multiplayerfeaturesystem.impl.forgefaker.payload.IForgePayload;

public interface IServerMetadata {

    IForgePayload tarasande_getForgePayload();
    void tarasande_setForgePayload(final IForgePayload forgePayload);

}
