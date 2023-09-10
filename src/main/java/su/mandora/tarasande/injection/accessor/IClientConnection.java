package su.mandora.tarasande.injection.accessor;

import net.minecraft.network.packet.Packet;

public interface IClientConnection {

    void tarasande_forceSend(Packet<?> packet);

}
