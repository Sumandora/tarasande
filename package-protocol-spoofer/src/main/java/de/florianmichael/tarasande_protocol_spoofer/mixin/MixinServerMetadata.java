package de.florianmichael.tarasande_protocol_spoofer.mixin;

import net.minecraft.server.ServerMetadata;
import de.florianmichael.tarasande_protocol_spoofer.spoofer.forgefaker.payload.IForgePayload;
import de.florianmichael.tarasande_protocol_spoofer.accessor.IServerMetadata;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerMetadata.class)
public class MixinServerMetadata implements IServerMetadata {

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
