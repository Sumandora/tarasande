package de.florianmichael.vialegacy.protocol.splitter;

import de.florianmichael.vialegacy.protocols.protocol1_4_5to1_4_3_pre.type.TypeRegistry_1_4_2;
import de.florianmichael.vialegacy.protocols.protocol1_7_5to1_6_4.type.TypeRegistry_1_6_4;
import de.florianmichael.vialegacy.protocols.protocol1_3_2to1_2_5.type.impl.NBTItems;
import io.netty.buffer.ByteBuf;

public class TransformInstanceUtil {

    public void read1_6_4_MetadataList(final ByteBuf buf) {
        try {
            TypeRegistry_1_6_4.METADATA_LIST.read(buf);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void read1_4_2_MetadataList(final ByteBuf buf) {
        try {
            TypeRegistry_1_4_2.METADATA_LIST.read(buf);
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
