/*
 * Copyright (c) FlorianMichael as EnZaXD 2022
 * Created on 08.04.22, 22:37
 *
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.0--
 *
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license.
 */

package de.florianmichael.vialegacy.protocols.protocol1_7_10to1_7_5;

import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.base.ClientboundLoginPackets;
import de.florianmichael.vialegacy.protocols.protocol1_8to1_7_10.type.TypeRegistry1_7_6_10;
import de.florianmichael.vialegacy.api.EnZaProtocol;
import de.florianmichael.vialegacy.protocols.protocol1_8to1_7_10.ClientboundLoginPackets1_7_2;
import de.florianmichael.vialegacy.protocols.protocol1_8to1_7_10.ClientboundPackets1_7_10;
import de.florianmichael.vialegacy.protocols.protocol1_8to1_7_10.ServerboundPackets1_7_10;

public class Protocol1_7_10to1_7_5 extends EnZaProtocol<ClientboundPackets1_7_5, ClientboundPackets1_7_10, ServerboundPackets1_7_5, ServerboundPackets1_7_10> {

    public static final DashUuidAdder ADD_DASHES = new DashUuidAdder();

    public Protocol1_7_10to1_7_5() {
        super(ClientboundPackets1_7_5.class, ClientboundPackets1_7_10.class, ServerboundPackets1_7_5.class, ServerboundPackets1_7_10.class);
    }

    @Override
    protected void registerPackets() {
        super.registerPackets();
        this.registerClientbound(State.LOGIN, ClientboundLoginPackets1_7_2.LOGIN_SUCCESS.getId(), ClientboundLoginPackets.GAME_PROFILE.getId(), new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.STRING, ADD_DASHES);
                map(Type.STRING);
            }
        });

        this.registerClientbound(ClientboundPackets1_7_5.SPAWN_PLAYER, new CustomPacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT);
                map(Type.STRING, ADD_DASHES);
                map(Type.STRING);
                handler((pw) -> pw.write(Type.VAR_INT, 0));
                map(Type.INT, 3);
                map(Type.BYTE, 2);
                map(Type.SHORT);
                map(TypeRegistry1_7_6_10.METADATA_LIST);
            }
        });
    }
}
