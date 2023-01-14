package de.florianmichael.viabeta.baseprotocol;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.AbstractSimpleProtocol;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.protocols.base.ClientboundStatusPackets;
import com.viaversion.viaversion.protocols.base.ServerboundHandshakePackets;
import com.viaversion.viaversion.protocols.base.ServerboundStatusPackets;
import de.florianmichael.viabeta.ViaBeta;
import de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.ClientboundPackets1_6_4;
import de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.Protocol1_7_2_5to1_6_4;
import de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.ServerboundPackets1_6_4;
import de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.storage.HandshakeStorage;
import de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.storage.ProtocolMetadataStorage;
import de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.type.Type1_6_4;

import java.util.logging.Level;

public class BaseProtocol1_6 extends AbstractSimpleProtocol {

    public static final BaseProtocol1_6 INSTANCE = new BaseProtocol1_6();

    private BaseProtocol1_6() {
        this.initialize();
    }

    @Override
    protected void registerPackets() {
        super.registerPackets();

        this.registerClientbound(State.STATUS, ClientboundPackets1_6_4.DISCONNECT.getId(), ClientboundStatusPackets.STATUS_RESPONSE.getId(), new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    final String reason = wrapper.read(Type1_6_4.STRING); // reason
                    try {
                        final String[] motdParts = reason.split("\0");
                        final JsonObject rootObject = new JsonObject();
                        final JsonObject descriptionObject = new JsonObject();
                        final JsonObject playersObject = new JsonObject();
                        final JsonObject versionObject = new JsonObject();

                        descriptionObject.addProperty("text", motdParts[3]);
                        playersObject.addProperty("max", Integer.parseInt(motdParts[5]));
                        playersObject.addProperty("online", Integer.parseInt(motdParts[4]));
                        versionObject.addProperty("name", motdParts[2]);
                        versionObject.addProperty("protocol", Integer.parseInt(motdParts[1]));
                        rootObject.add("description", descriptionObject);
                        rootObject.add("players", playersObject);
                        rootObject.add("version", versionObject);

                        wrapper.write(Type.STRING, rootObject.toString());
                    } catch (Throwable e) {
                        ViaBeta.getPlatform().getLogger().log(Level.WARNING, "Could not parse 1.6.4 ping: " + reason, e);
                        wrapper.cancel();
                    }
                });
            }
        });

        this.registerServerbound(State.HANDSHAKE, ServerboundHandshakePackets.CLIENT_INTENTION.getId(), ServerboundHandshakePackets.CLIENT_INTENTION.getId(), new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    wrapper.cancel();
                    wrapper.read(Type.VAR_INT); // protocolVersion
                    final String hostname = wrapper.read(Type.STRING); // hostName
                    final int port = wrapper.read(Type.UNSIGNED_SHORT); // port
                    wrapper.user().put(new HandshakeStorage(wrapper.user(), hostname, port));
                });
            }
        });
        this.registerServerbound(State.STATUS, ServerboundPackets1_6_4.SERVER_PING.getId(), ServerboundStatusPackets.STATUS_REQUEST.getId(), new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    final HandshakeStorage handshakeStorage = wrapper.user().get(HandshakeStorage.class);
                    final String ip = handshakeStorage.getHostname();
                    final int port = handshakeStorage.getPort();
                    wrapper.write(Type.UNSIGNED_BYTE, (short) 1); // always 1
                    wrapper.write(Type.UNSIGNED_BYTE, (short) 250); // packet id
                    wrapper.write(Type1_6_4.STRING, "MC|PingHost"); // channel
                    wrapper.write(Type.UNSIGNED_SHORT, 3 + 2 * ip.length() + 4); // length
                    wrapper.write(Type.UNSIGNED_BYTE, (short) (-wrapper.user().getProtocolInfo().getServerProtocolVersion() >> 2)); // protocol Id
                    wrapper.write(Type1_6_4.STRING, ip); // hostname
                    wrapper.write(Type.INT, port); // port
                });
            }
        });
        this.registerServerbound(State.STATUS, -1, ServerboundStatusPackets.PING_REQUEST.getId(), new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    wrapper.cancel();
                    final PacketWrapper pong = PacketWrapper.create(ClientboundStatusPackets.PONG_RESPONSE, wrapper.user());
                    pong.write(Type.LONG, wrapper.read(Type.LONG)); // start time
                    pong.send(Protocol1_7_2_5to1_6_4.class);
                });
            }
        });
    }

    @Override
    public void init(UserConnection userConnection) {
        super.init(userConnection);

        userConnection.put(new ProtocolMetadataStorage(userConnection));
    }

    @Override
    public boolean isBaseProtocol() {
        return true;
    }
}
