package de.florianmichael.vialegacy;

import com.viaversion.viaversion.api.connection.UserConnection;
import io.netty.channel.Channel;

public interface IViaLegacyProvider {

    int currentVersion();

    void fixPipelineOrder_1_6(final Channel channel, final String decoder, final String encoder);
    void rewriteElements_1_6(final UserConnection connection, final Channel channel, final String decoder, final String encoder);

    void sendJoinServer_1_2_5(final String serverId);
}
