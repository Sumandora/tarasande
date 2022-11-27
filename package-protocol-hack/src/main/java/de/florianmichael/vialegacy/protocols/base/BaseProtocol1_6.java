/**
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.2--
 *
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license. The creator assumes no responsibility for any infringements
 * that have arisen, are arising or will arise from this project / file. If this licence is used anywhere,
 * the latest version published by the author Florian Michael (aka EnZaXD) always applies automatically.
 *
 * Changelog:
 *     v1.0:
 *         Added License
 *     v1.1:
 *         Ownership withdrawn
 *     v1.2:
 *         Version-independent validity and automatic renewal
 */

package de.florianmichael.vialegacy.protocols.base;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.base.ServerboundHandshakePackets;

public class BaseProtocol1_6 extends AbstractProtocol {

    public static final BaseProtocol1_6 INSTANCE = new BaseProtocol1_6();

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
