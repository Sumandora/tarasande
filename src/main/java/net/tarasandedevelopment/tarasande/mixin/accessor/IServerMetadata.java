package net.tarasandedevelopment.tarasande.mixin.accessor;

import net.tarasandedevelopment.tarasande.screen.clientmenu.forgefaker.payload.IForgePayload;

public interface IServerMetadata {

    IForgePayload getForgePayload();
    void setForgePayload(final IForgePayload forgePayload);
}
