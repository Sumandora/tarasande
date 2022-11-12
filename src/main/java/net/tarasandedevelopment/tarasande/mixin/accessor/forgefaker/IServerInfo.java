package net.tarasandedevelopment.tarasande.mixin.accessor.forgefaker;

import net.tarasandedevelopment.tarasande.systems.screen.clientmenu.clientmenu.forgefaker.payload.IForgePayload;

public interface IServerInfo {

    IForgePayload tarasande_getForgePayload();

    void tarasande_setForgePayload(final IForgePayload forgePayload);
}
