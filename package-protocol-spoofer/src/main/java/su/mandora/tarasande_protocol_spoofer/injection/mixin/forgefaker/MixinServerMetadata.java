package su.mandora.tarasande_protocol_spoofer.injection.mixin.forgefaker;

import su.mandora.tarasande_protocol_spoofer.injection.accessor.IServerMetadata;
import su.mandora.tarasande_protocol_spoofer.tarasandevalues.forge.payload.IForgePayload;
import net.minecraft.server.ServerMetadata;
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
