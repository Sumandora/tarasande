package de.florianmichael.vialegacy;

import com.viaversion.viaversion.api.connection.UserConnection;
import de.florianmichael.vialegacy.api.profile.GameProfile;
import de.florianmichael.vialegacy.protocols.base.BaseProtocol1_6;
import io.netty.channel.Channel;

public interface IViaLegacyProvider {

    int currentVersion();

    GameProfile profile_1_7();

    void fixPipelineOrder_1_6(final Channel channel, final String decoder, final String encoder);
    void rewriteElements_1_6(final UserConnection connection, final Channel channel, final String decoder, final String encoder);

    void sendJoinServer_1_2_5(final String serverId);
}
