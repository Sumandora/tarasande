package de.florianmichael.vialegacy.protocol.splitter;

import de.florianmichael.vialegacy.api.type.TypeRegistry1_7_6_10;
import de.florianmichael.vialegacy.api.type.TypeRegistry_1_6_4;
import de.florianmichael.vialegacy.api.type._1_4_2.TypeRegistry1_4_2;
import io.netty.buffer.ByteBuf;

public class TransformInstanceUtil {

    public void read1_7_10_CompressedNbtItem(final ByteBuf buf) {
        try {
            TypeRegistry1_7_6_10.COMPRESSED_NBT_ITEM.read(buf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void read1_7_10_CompressedNbt(final ByteBuf buf) {
        try {
            TypeRegistry1_7_6_10.COMPRESSED_NBT.read(buf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void read1_6_4_MetadataList(final ByteBuf buf) {
        try {
            TypeRegistry_1_6_4.METADATA_LIST.read(buf);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void read1_4_2_MetadataList(final ByteBuf buf) {
        try {
            TypeRegistry1_4_2.METADATA_LIST.read(buf);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void readString(final ByteBuf buf) {
        final short length = buf.readShort();

        for (int i = 0; i < length; i++)
            buf.readChar();
    }
}
