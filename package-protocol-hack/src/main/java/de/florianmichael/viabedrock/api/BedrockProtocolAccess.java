package de.florianmichael.viabedrock.api;

import de.florianmichael.viabedrock.ViaBedrock;
import de.florianmichael.viabedrock.baseprotocol.BedrockSessionBaseProtocol;

import java.net.InetSocketAddress;

public class BedrockProtocolAccess {

    public static String getConnectionState_Bedrock_1_19_51(final InetSocketAddress currentConnection) {
        if (!BedrockSessionBaseProtocol.INSTANCE.getConnectionProgress().containsKey(currentConnection)) return null;

        return ViaBedrock.PREFIX_1_7 + BedrockSessionBaseProtocol.INSTANCE.getConnectionProgress().get(currentConnection);
    }
}
