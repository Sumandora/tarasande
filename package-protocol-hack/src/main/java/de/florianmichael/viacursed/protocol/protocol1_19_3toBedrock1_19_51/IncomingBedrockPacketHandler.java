package de.florianmichael.viacursed.protocol.protocol1_19_3toBedrock1_19_51;

import com.nukkitx.protocol.bedrock.handler.BedrockPacketHandler;
import com.nukkitx.protocol.bedrock.packet.DisconnectPacket;
import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;

public class IncomingBedrockPacketHandler extends StoredObject implements BedrockPacketHandler {

    public IncomingBedrockPacketHandler(UserConnection user) {
        super(user);
    }

    @Override
    public boolean handle(DisconnectPacket packet) {
        System.out.println(packet.getKickMessage());
        return false;
    }
}
