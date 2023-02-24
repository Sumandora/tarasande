package de.florianmichael.tarasande_protocol_spoofer.injection.mixin.forgefaker;

import de.florianmichael.tarasande_protocol_spoofer.injection.accessor.IServerInfo;
import de.florianmichael.tarasande_protocol_spoofer.tarasandevalues.forge.payload.IForgePayload;
import net.minecraft.client.network.ServerInfo;
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
