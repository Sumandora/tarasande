package net.tarasandedevelopment.tarasande.mixin.accessor;

import net.minecraft.network.Packet;

public interface IClientConnection {

    void tarasande_addForcePacket(Packet<?> packet);

    void tarasande_forceSend(Packet<?> packet);

}
