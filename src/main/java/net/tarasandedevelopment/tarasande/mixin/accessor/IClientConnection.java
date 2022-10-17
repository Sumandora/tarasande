package net.tarasandedevelopment.tarasande.mixin.accessor;

import io.netty.channel.Channel;
import net.minecraft.network.Packet;

public interface IClientConnection {
    Channel tarasande_getChannel();

    void tarasande_addForcePacket(Packet<?> packet);

    void tarasande_forceSend(Packet<?> packet);
}
