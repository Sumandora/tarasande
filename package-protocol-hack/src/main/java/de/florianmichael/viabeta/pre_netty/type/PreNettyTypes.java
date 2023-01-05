package de.florianmichael.viabeta.pre_netty.type;

import de.florianmichael.viabeta.protocol.protocol1_3_1_2to1_2_4_5.data.NbtItemList_1_2_5;
import io.netty.buffer.ByteBuf;

public class PreNettyTypes {

    public static void readString(final ByteBuf buffer) {
        short s = buffer.readShort();
        for (int i = 0; i < s; i++) buffer.readShort();
    }

    public static void readUTF(final ByteBuf buffer) {
        int l = buffer.readUnsignedShort();
        for (int i = 0; i < l; i++) buffer.readByte();
    }

    public static void readString64(final ByteBuf buffer) {
        for (int i = 0; i < 64; i++) buffer.readByte();
    }

    public static void readItemStack1_3_1(final ByteBuf buffer) {
        short s = buffer.readShort();
        if (s >= 0) {
            buffer.readByte();
            buffer.readShort();
            readTag(buffer);
        }
    }

    public static void readItemStack1_0(final ByteBuf buffer) {
        short s = buffer.readShort();
        if (s >= 0) {
            buffer.readByte();
            buffer.readShort();
            if (NbtItemList_1_2_5.hasNbt(s)) {
                readTag(buffer);
            }
        }
    }

    public static void readItemStackb1_2(final ByteBuf buffer) {
        short s = buffer.readShort();
        if (s >= 0) {
            buffer.readByte();
            buffer.readShort();
        }
    }

    public static void readItemStackb1_1(final ByteBuf buffer) {
        short s = buffer.readShort();
        if (s >= 0) {
            buffer.readByte();
            buffer.readByte();
        }
    }

    public static void readByteArray(final ByteBuf buffer) {
        short s = buffer.readShort();
        for (int i = 0; i < s; i++) buffer.readByte();
    }

    public static void readByteArray1024(final ByteBuf buffer) {
        for (int i = 0; i < 1024; i++) buffer.readByte();
    }

    public static void readTag(final ByteBuf buffer) {
        int s = buffer.readShort();
        for (int i = 0; i < s; i++) buffer.readByte();
    }

    public static void readEntityMetadata1_4_4(final ByteBuf buffer) {
        for (byte b = buffer.readByte(); b != 127; b = buffer.readByte()) {
            int i = (b & 224) >> 5;
            switch (i) {
                case 0 -> buffer.readByte();
                case 1 -> buffer.readShort();
                case 2 -> buffer.readInt();
                case 3 -> buffer.readFloat();
                case 4 -> readString(buffer);
                case 5 -> readItemStack1_3_1(buffer);
                case 6 -> {
                    buffer.readInt();
                    buffer.readInt();
                    buffer.readInt();
                }
            }
        }
    }

    public static void readEntityMetadata1_4_2(final ByteBuf buffer) {
        for (byte b = buffer.readByte(); b != 127; b = buffer.readByte()) {
            int i = (b & 224) >> 5;
            switch (i) {
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

    public static void readEntityMetadatab1_5(final ByteBuf buffer) {
        for (byte b = buffer.readByte(); b != 127; b = buffer.readByte()) {
            int i = (b & 224) >> 5;
            switch (i) {
                case 0 -> buffer.readByte();
                case 1 -> buffer.readShort();
                case 2 -> buffer.readInt();
                case 3 -> buffer.readFloat();
                case 4 -> readString(buffer);
                case 5 -> {
                    buffer.readShort();
                    buffer.readByte();
                    buffer.readShort();
                }
                case 6 -> {
                    buffer.readInt();
                    buffer.readInt();
                    buffer.readInt();
                }
            }
        }
    }

    public static void readEntityMetadatab1_3(final ByteBuf buffer) {
        for (byte b = buffer.readByte(); b != 127; b = buffer.readByte()) {
            int i = (b & 224) >> 5;
            switch (i) {
                case 0 -> buffer.readByte();
                case 1 -> buffer.readShort();
                case 2 -> buffer.readInt();
                case 3 -> buffer.readFloat();
                case 4 -> readUTF(buffer);
                case 5 -> {
                    buffer.readShort();
                    buffer.readByte();
                    buffer.readShort();
                }
                case 6 -> {
                    buffer.readInt();
                    buffer.readInt();
                    buffer.readInt();
                }
            }
        }
    }

    public static void readEntityMetadatab1_2(final ByteBuf buffer) {
        for (byte b = buffer.readByte(); b != 127; b = buffer.readByte()) {
            int i = (b & 224) >> 5;
            switch (i) {
                case 0 -> buffer.readByte();
                case 1 -> buffer.readShort();
                case 2 -> buffer.readInt();
                case 3 -> buffer.readFloat();
                case 4 -> readUTF(buffer);
                case 5 -> {
                    buffer.readShort();
                    buffer.readByte();
                    buffer.readShort();
                }
            }
        }
    }
}
