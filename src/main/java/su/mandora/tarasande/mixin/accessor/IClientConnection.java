package su.mandora.tarasande.mixin.accessor;

import io.netty.channel.Channel;

public interface IClientConnection {
    Channel getChannel();
}
