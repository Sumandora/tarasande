package de.florianmichael.tarasande_protocol_spoofer.mixin;

import net.minecraft.client.network.ServerInfo;
import de.florianmichael.tarasande_protocol_spoofer.multiplayerfeature.forgefaker.payload.IForgePayload;
import de.florianmichael.tarasande_protocol_spoofer.accessor.IServerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerInfo.class)
public class MixinServerInfo implements IServerInfo {

    @Unique
    private IForgePayload tarasande_forgePayload;

    @Override
    public IForgePayload tarasande_getForgePayload() {
        return this.tarasande_forgePayload;
    }

    @Override
    public void tarasande_setForgePayload(IForgePayload forgePayload) {
        this.tarasande_forgePayload = forgePayload;
    }
}
