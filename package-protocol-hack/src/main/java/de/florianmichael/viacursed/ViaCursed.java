package de.florianmichael.viacursed;

import de.florianmichael.viacursed.base.ViaCursedConfig;
import de.florianmichael.viacursed.base.ViaCursedPlatform;
import de.florianmichael.viacursed.protocol.protocol1_19_3toBedrock1_19_51.baseprotocol.BedrockSessionBaseProtocol;

import java.net.InetSocketAddress;

public class ViaCursed {
    private static ViaCursedPlatform platform;
    private static ViaCursedConfig config;

    public static void init(final ViaCursedPlatform platform, final ViaCursedConfig config) {
        if (ViaCursed.platform != null) {
            throw new IllegalStateException("ViaCursed has already loaded the platform");
        }

        ViaCursed.platform = platform;
        ViaCursed.config = config;
    }

    public static String getConnectionState_Bedrock_1_19_51(final InetSocketAddress currentConnection) {
        if (!BedrockSessionBaseProtocol.INSTANCE.getConnectionProgress().containsKey(currentConnection)) return null;

        return "§c[ViaCursed]§f " + BedrockSessionBaseProtocol.INSTANCE.getConnectionProgress().get(currentConnection);
    }

    public static ViaCursedPlatform getPlatform() {
        return ViaCursed.platform;
    }

    public static ViaCursedConfig getConfig() {
        return config;
    }
}
