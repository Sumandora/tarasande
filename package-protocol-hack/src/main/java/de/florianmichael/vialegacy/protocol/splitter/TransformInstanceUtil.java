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

package de.florianmichael.vialegacy.protocol.splitter;

import de.florianmichael.vialegacy.protocols.protocol1_3_1_2to1_2_4_5.data.NBTItems;
import de.florianmichael.vialegacy.protocols.protocol1_4_4_5to1_4_3_pre.type.Types1_4_2;
import de.florianmichael.vialegacy.protocols.protocol1_7_0_1_preto1_6_4.type.Types1_6_4;
import io.netty.buffer.ByteBuf;

public class TransformInstanceUtil {

    public void read1_6_4_MetadataList(final ByteBuf buf) {
        try {
            Types1_6_4.METADATA_LIST.read(buf);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void read1_4_2_MetadataList(final ByteBuf buf) {
        try {
            Types1_4_2.METADATA_LIST.read(buf);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void readNbt(final ByteBuf buffer) {
        int length = buffer.readShort();
        if (length < 0) {
            return;
        }
        for (int i = 0; i < length; i++) {
            buffer.readByte();
        }
    }

    public void read1_7_10_ItemStack(final ByteBuf buf) {
        final short id = buf.readShort();
        if (id >= 0) {
            buf.readByte();
            buf.readShort();
            readNbt(buf);
        }
    }

    public void read1_2_5_ItemStack(final ByteBuf buffer) {
        short x = buffer.readShort();
        if (x >= 0) {
            buffer.readByte();
            buffer.readShort();
            if (NBTItems.map.getOrDefault((int) x, false))
                readNbt(buffer);
        }
    }

    public void read1_2_5_DataWatcher(final ByteBuf buffer) {
        for (byte var2 = buffer.readByte(); var2 != 127; var2 = buffer.readByte()) {
            int var3 = (var2 & 224) >> 5;
            switch (var3) {
                case 0 -> buffer.readByte();
                case 1 -> buffer.readShort();
                case 2 -> buffer.readInt();
                case 3 -> buffer.readFloat();
                case 4 -> readString(buffer);
                case 5 -> {
                    short x = buffer.readShort();
                    if (x > -1) {
                        buffer.readByte();
                        buffer.readShort();
                    }
                }
                case 6 -> {
                    buffer.readInt();
                    buffer.readInt();
                    buffer.readInt();
                }
            }
        }
    }

    public void readString(final ByteBuf buf) {
        final short length = buf.readShort();

        for (int i = 0; i < length; i++)
            buf.readChar();
    }
}
