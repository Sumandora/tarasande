package net.tarasandedevelopment.tarasande.mixin.accessor.forgefaker;

import net.tarasandedevelopment.tarasande.screen.clientmenu.forgefaker.payload.IForgePayload;

public interface IServerMetadata {

    IForgePayload getForgePayload();
    void setForgePayload(final IForgePayload forgePayload);
}
