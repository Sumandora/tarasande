package de.florianmichael.viacursed.protocol.protocol1_19_3toBedrock1_19_51;

import com.nukkitx.protocol.bedrock.handler.BedrockPacketHandler;
import com.nukkitx.protocol.bedrock.packet.*;
import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import de.florianmichael.viacursed.netty.BedrockConnection;
import de.florianmichael.viacursed.protocol.protocol1_19_3toBedrock1_19_51.storage.PlayerStorage;

public class ClientboundPacketHandler implements BedrockPacketHandler {

    /**
     * Tarasande -> Local Proxy -> Server
     * Bedrock Native -> Local Proxy -> Server
     *
     * tarasande -> localhost:10 -> server
     *
     */

    private BedrockConnection bedrockConnection = null;
    private UserConnection connection = null;

    public void connect(final BedrockConnection bedrockConnection, final UserConnection connection) {
        this.bedrockConnection = bedrockConnection;
        this.connection = connection;
    }

    @Override
    public boolean handle(StartGamePacket packet) {
        getConnection().put(new PlayerStorage(getConnection()));

        return false;
    }

    @Override
    public boolean handle(DisconnectPacket packet) {
        System.out.println(packet.getKickMessage());
        System.out.println("Test");
        return false;
    }

    @Override
    public boolean handle(ServerToClientHandshakePacket packet) {
        System.out.println("UWUWUUWUWUWUWUW");
        System.out.println(packet.getJwt());
        return false;
    }

    public BedrockConnection getBedrockConnection() {
        if (bedrockConnection == null) throw new IllegalStateException("ClientboundPacketHandler is not connected to ViaCursed");
        return bedrockConnection;
    }

    public UserConnection getConnection() {
        if (connection == null) throw new IllegalStateException("ClientboundPacketHandler is not connected to ViaCursed");
        return connection;
    }
}
