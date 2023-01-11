package de.florianmichael.viacursed.api;

import de.florianmichael.viacursed.ViaCursed;
import de.florianmichael.viacursed.protocol.protocol1_19_3toBedrock1_19_51.baseprotocol.BedrockSessionBaseProtocol;

import java.net.InetSocketAddress;

public class CursedProtocolAccess {

    public static String getConnectionState_Bedrock_1_19_51(final InetSocketAddress currentConnection) {
        if (!BedrockSessionBaseProtocol.INSTANCE.getConnectionProgress().containsKey(currentConnection)) return null;

        return ViaCursed.PREFIX_1_7 + BedrockSessionBaseProtocol.INSTANCE.getConnectionProgress().get(currentConnection);
    }
}
