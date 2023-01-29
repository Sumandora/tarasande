package de.florianmichael.viabedrock.api;

import com.nukkitx.protocol.bedrock.BedrockPacketCodec;
import com.nukkitx.protocol.bedrock.v560.Bedrock_v560;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;

public class BedrockProtocols {

    public static final String VERSION_NAME = "1.19.51";
    public static final ProtocolVersion VIA_PROTOCOL_VERSION = new ProtocolVersion(560 << 2, "Bedrock edition v" + VERSION_NAME);
    public static final BedrockPacketCodec CODEC = Bedrock_v560.V560_CODEC;
}
