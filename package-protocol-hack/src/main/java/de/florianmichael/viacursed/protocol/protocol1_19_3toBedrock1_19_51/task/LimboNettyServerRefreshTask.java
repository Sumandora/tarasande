package de.florianmichael.viacursed.protocol.protocol1_19_3toBedrock1_19_51.task;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import de.florianmichael.viacursed.protocol.protocol1_19_3toBedrock1_19_51.Protocol1_19_3toBedrock1_19_51;
import de.florianmichael.viacursed.protocol.protocol1_19_3toBedrock1_19_51.storage.BedrockSessionStorage;

@SuppressWarnings("ConstantValue")
public class LimboNettyServerRefreshTask implements Runnable {

    @Override
    public void run() {
        for (UserConnection info : Via.getManager().getConnectionManager().getConnections()) {
            final BedrockSessionStorage bedrockSessionStorage = info.get(BedrockSessionStorage.class);
            if (bedrockSessionStorage == null) continue;

            if (!bedrockSessionStorage.isBedrockConnected()) {
                Protocol1_19_3toBedrock1_19_51.LIMBO_NETTY_SERVERS.remove(bedrockSessionStorage.targetAddress).stopServer();
            }
        }
    }
}
