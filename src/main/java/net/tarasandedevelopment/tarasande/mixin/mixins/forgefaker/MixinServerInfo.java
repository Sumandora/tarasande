package net.tarasandedevelopment.tarasande.mixin.mixins.forgefaker;

import net.minecraft.client.network.ServerInfo;
import net.tarasandedevelopment.tarasande.mixin.accessor.IServerInfo;
import net.tarasandedevelopment.tarasande.screen.clientmenu.forgefaker.payload.IForgePayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerInfo.class)
public class MixinServerInfo implements IServerInfo {

    @Unique
    private IForgePayload forgePayload;

    @Override
    public IForgePayload getForgePayload() {
        return this.forgePayload;
    }

    @Override
    public void setForgePayload(IForgePayload forgePayload) {
        this.forgePayload = forgePayload;
    }
}
