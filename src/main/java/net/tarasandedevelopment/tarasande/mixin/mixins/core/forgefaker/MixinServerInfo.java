package net.tarasandedevelopment.tarasande.mixin.mixins.core.forgefaker;

import net.minecraft.client.network.ServerInfo;
import net.tarasandedevelopment.tarasande.mixin.accessor.forgefaker.IServerInfo;
import net.tarasandedevelopment.tarasande.screen.clientmenu.forgefaker.payload.IForgePayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerInfo.class)
public class MixinServerInfo implements IServerInfo {

    @Unique
    private IForgePayload tarasande_forgePayload;

    @Override
    public IForgePayload getForgePayload() {
        return this.tarasande_forgePayload;
    }

    @Override
    public void setForgePayload(IForgePayload forgePayload) {
        this.tarasande_forgePayload = forgePayload;
    }
}