/*
 * This file is part of ViaLegacy - https://github.com/RaphiMC/ViaLegacy
 * Copyright (C) 2023 RK_01/RaphiMC and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import de.florianmichael.viabeta.pre_netty.viaversion.PreNettyPacketType;
import io.netty.buffer.ByteBuf;

import java.util.function.BiConsumer;

import static de.florianmichael.viabeta.pre_netty.type.PreNettyTypes.*;

public enum ServerboundPackets1_6_4 implements ServerboundPacketType, PreNettyPacketType {

    KEEP_ALIVE(0, (user, buf) -> {
        buf.skipBytes(4);
    }),
    LOGIN(1, (user, buf) -> {
        buf.skipBytes(4);
        readString(buf);
        buf.skipBytes(5);
    }),
    CLIENT_PROTOCOL(2, (user, buf) -> {
        buf.skipBytes(1);
        readString(buf);
        readString(buf);
        buf.skipBytes(4);
    }),
    CHAT_MESSAGE(3, (user, buf) -> {
        readString(buf);
    }),
    INTERACT_ENTITY(7, (user, buf) -> {
        buf.skipBytes(9);
    }),
    RESPAWN(9, (user, buf) -> {
        buf.skipBytes(8);
        readString(buf);
    }),
    PLAYER_MOVEMENT(10, (user, buf) -> {
        buf.skipBytes(1);
    }),
    PLAYER_POSITION(11, (user, buf) -> {
        buf.skipBytes(33);
    }),
    PLAYER_ROTATION(12, (user, buf) -> {
        buf.skipBytes(9);
    }),
    PLAYER_POSITION_AND_ROTATION(13, (user, buf) -> {
        buf.skipBytes(41);
    }),
    PLAYER_DIGGING(14, (user, buf) -> {
        buf.skipBytes(11);
    }),
    PLAYER_BLOCK_PLACEMENT(15, (user, buf) -> {
        buf.skipBytes(10);
        readItemStack1_3_1(buf);
        buf.skipBytes(3);
    }),
    HELD_ITEM_CHANGE(16, (user, buf) -> {
        buf.skipBytes(2);
    }),
    ANIMATION(18, (user, buf) -> {
        buf.skipBytes(5);
    }),
    ENTITY_ACTION(19, (user, buf) -> {
        buf.skipBytes(9);
    }),
    STEER_VEHICLE(27, (user, buf) -> {
        buf.skipBytes(8);
        buf.readBoolean();
        buf.readBoolean();
    }),
    CLOSE_WINDOW(101, (user, buf) -> {
        buf.skipBytes(1);
    }),
    CLICK_WINDOW(102, (user, buf) -> {
        buf.skipBytes(7);
        readItemStack1_3_1(buf);
    }),
    WINDOW_CONFIRMATION(106, (user, buf) -> {
        buf.skipBytes(4);
    }),
    CREATIVE_INVENTORY_ACTION(107, (user, buf) -> {
        buf.skipBytes(2);
        readItemStack1_3_1(buf);
    }),
    CLICK_WINDOW_BUTTON(108, (user, buf) -> {
        buf.skipBytes(2);
    }),
    UPDATE_SIGN(130, (user, buf) -> {
        buf.skipBytes(10);
        readString(buf);
        readString(buf);
        readString(buf);
        readString(buf);
    }),
    PLAYER_ABILITIES(202, (user, buf) -> {
        buf.skipBytes(9);
    }),
    TAB_COMPLETE(203, (user, buf) -> {
        readString(buf);
    }),
    CLIENT_SETTINGS(204, (user, buf) -> {
        readString(buf);
        buf.skipBytes(3);
        buf.readBoolean();
    }),
    CLIENT_STATUS(205, (user, buf) -> {
        buf.skipBytes(1);
    }),
    PLUGIN_MESSAGE(250, (user, buf) -> {
        readString(buf);
        short s = buf.readShort();
        for (int i = 0; i < s; i++) buf.readByte();
    }),
    SHARED_KEY(252, (user, buf) -> {
        readByteArray(buf);
        readByteArray(buf);
    }),
    SERVER_PING(254, (user, buf) -> {
        buf.skipBytes(2);
        readString(buf);
        buf.skipBytes(2);
        byte x = buf.readByte();
        if (x >= 73) {
            readString(buf);
            buf.skipBytes(4);
        }
    }),
    DISCONNECT(255, (user, buf) -> {
        readString(buf);
    });

    private static final ServerboundPackets1_6_4[] REGISTRY = new ServerboundPackets1_6_4[256];

    static {
        for (ServerboundPackets1_6_4 packet : values()) {
            REGISTRY[packet.id] = packet;
        }
    }

    public static ServerboundPackets1_6_4 getPacket(final int id) {
        return REGISTRY[id];
    }

    private final int id;
    private final BiConsumer<UserConnection, ByteBuf> packetReader;

    ServerboundPackets1_6_4(final int id, final BiConsumer<UserConnection, ByteBuf> packetReader) {
        this.id = id;
        this.packetReader = packetReader;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return name();
    }

    @Override
    public BiConsumer<UserConnection, ByteBuf> getPacketReader() {
        return this.packetReader;
    }

}
