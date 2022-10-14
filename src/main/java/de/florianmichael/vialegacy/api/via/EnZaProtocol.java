/*
 * Copyright (c) FlorianMichael as EnZaXD 2022
 * Created on 6/24/22, 12:56 PM
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

package de.florianmichael.vialegacy.api.via;

import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.protocol.remapper.TypeRemapper;
import com.viaversion.viaversion.api.protocol.remapper.ValueReader;
import com.viaversion.viaversion.api.type.Type;

public class EnZaProtocol<C1 extends ClientboundPacketType, C2 extends ClientboundPacketType, S1 extends ServerboundPacketType, S2 extends ServerboundPacketType> extends AbstractProtocol<C1, C2, S1, S2> {

    public EnZaProtocol() {
        super(null, null, null, null);
    }

    public EnZaProtocol(Class<C1> oldClientboundPacketEnum, Class<C2> clientboundPacketEnum, Class<S1> oldServerboundPacketEnum, Class<S2> serverboundPacketEnum) {
        super(oldClientboundPacketEnum, clientboundPacketEnum, oldServerboundPacketEnum, serverboundPacketEnum);
    }

    public static final ValueReader<Position> xyzToPosition = packetWrapper -> {
        final int x = packetWrapper.read(Type.INT);
        final int y = packetWrapper.read(Type.INT);
        final int z = packetWrapper.read(Type.INT);

        return new Position(x, y, z);
    };

    public static abstract class CustomPacketRemapper extends PacketRemapper {

        public void intToVarInt() {
            map(Type.INT, Type.VAR_INT);
        }

        public void varIntToInt() {
            map(Type.VAR_INT, Type.INT);
        }

        public void xyzToPosition() {
            map(xyzToPosition, new TypeRemapper<>(Type.POSITION));
        }

        public void map(final Type<?> type, final int j) {
            for (int i = 0; i < j; i++)
                map(type);
        }
    }
}
