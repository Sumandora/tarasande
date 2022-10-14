package de.florianmichael.vialegacy.protocols.base;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.base.ServerboundHandshakePackets;

public class BaseProtocol1_6 extends AbstractProtocol {

    public static final BaseProtocol1_6 INSTANCE = new BaseProtocol1_6();

    public BaseProtocol1_6() {
        this.initialize();
    }

    @Override
    protected void registerPackets() {
        super.registerPackets();

        this.registerServerbound(State.HANDSHAKE, ServerboundHandshakePackets.CLIENT_INTENTION.getId(), ServerboundHandshakePackets.CLIENT_INTENTION.getId(), new PacketRemapper() {
            @Override
            public void registerMap() {
                this.handler(packetWrapper -> {
                    packetWrapper.cancel();
                    packetWrapper.passthrough(Type.VAR_INT); // Protocol ID

                    final String hostname = packetWrapper.passthrough(Type.STRING);
                    final Integer port = packetWrapper.passthrough(Type.UNSIGNED_SHORT);

                    final HandshakeStorage handshakeStorage = packetWrapper.user().get(HandshakeStorage.class);

                    System.out.println(hostname + " " + port);

                    assert handshakeStorage != null;
                    handshakeStorage.hostname = hostname;
                    handshakeStorage.port = port;
                });
            }
        });
    }

    @Override
    public void init(UserConnection connection) {
        super.init(connection);

        connection.put(new HandshakeStorage(connection));
    }

    @Override
    public boolean isBaseProtocol() {
        return true;
    }
}
