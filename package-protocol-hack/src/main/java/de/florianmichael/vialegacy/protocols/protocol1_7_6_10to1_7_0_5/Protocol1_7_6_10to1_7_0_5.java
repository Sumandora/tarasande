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

package de.florianmichael.vialegacy.protocols.protocol1_7_6_10to1_7_0_5;

import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.protocol.remapper.ValueTransformer;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.base.ClientboundLoginPackets;
import de.florianmichael.vialegacy.protocols.protocol1_8_0_9to1_7_6_10.type.Types1_7_6_10;
import de.florianmichael.vialegacy.api.EnZaProtocol;
import de.florianmichael.vialegacy.protocols.protocol1_8_0_9to1_7_6_10.ClientboundPackets1_7_6_10;
import de.florianmichael.vialegacy.protocols.protocol1_8_0_9to1_7_6_10.ServerboundPackets1_7_6_10;

public class Protocol1_7_6_10to1_7_0_5 extends EnZaProtocol<ClientboundPackets1_7_0_5, ClientboundPackets1_7_6_10, ServerboundPackets1_7_0_5, ServerboundPackets1_7_6_10> {

    public static final ValueTransformer<String, String> ADD_DASHES = new ValueTransformer<>(Type.STRING) {
        @Override
        public String transform(PacketWrapper wrapper, String inputValue) {
            return inputValue.substring(0, 8) + "-" +
                    inputValue.substring(8, 12) + "-" +
                    inputValue.substring(12, 16) + "-" +
                    inputValue.substring(16, 20) + "-" +
                    inputValue.substring(20);
        }
    };

    public Protocol1_7_6_10to1_7_0_5() {
        super(ClientboundPackets1_7_0_5.class, ClientboundPackets1_7_6_10.class, ServerboundPackets1_7_0_5.class, ServerboundPackets1_7_6_10.class);
    }

    @Override
    protected void registerPackets() {
        super.registerPackets();
        this.registerClientbound(State.LOGIN, ClientboundLoginPackets.GAME_PROFILE.getId(), ClientboundLoginPackets.GAME_PROFILE.getId(), new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.STRING, ADD_DASHES);
                map(Type.STRING);
            }
        });

        this.registerClientbound(ClientboundPackets1_7_0_5.SPAWN_PLAYER, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT);
                map(Type.STRING, ADD_DASHES);
                map(Type.STRING);
                handler(wrapper -> wrapper.write(Type.VAR_INT, 0));
                map(Type.INT);
                map(Type.INT);
                map(Type.INT);
                map(Type.BYTE);
                map(Type.BYTE);
                map(Type.SHORT);
                map(Types1_7_6_10.METADATA_LIST);
            }
        });
    }
}
