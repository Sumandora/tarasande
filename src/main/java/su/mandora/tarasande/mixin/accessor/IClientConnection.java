package su.mandora.tarasande.mixin.accessor;

import io.netty.channel.Channel;
import net.minecraft.network.Packet;

public interface IClientConnection {
    Channel getChannel();
    void forceSend(Packet<?> packet);
}
