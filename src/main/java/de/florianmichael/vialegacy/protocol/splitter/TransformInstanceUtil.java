package de.florianmichael.vialegacy.protocol.splitter;

import de.florianmichael.vialegacy.api.type.TypeRegistry1_7_6_10;
import de.florianmichael.vialegacy.api.type.TypeRegistry_1_6_4;
import io.netty.buffer.ByteBuf;

public class TransformInstanceUtil {

    private final ByteBuf byteBuf;

    public TransformInstanceUtil(final ByteBuf byteBuf) {
        this.byteBuf = byteBuf;
    }

    public void read1_7_10_CompressedNbtItem() {
        try {
            TypeRegistry1_7_6_10.COMPRESSED_NBT_ITEM.read(this.byteBuf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void read1_7_10_CompressedNbt() {
        try {
            TypeRegistry1_7_6_10.COMPRESSED_NBT.read(this.byteBuf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void read1_6_4_MetadataList() {
        try {
            TypeRegistry_1_6_4.METADATA_LIST.read(this.byteBuf);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void readString() {
        final short length = this.byteBuf.readShort();

        for (int i = 0; i < length; i++)
            this.byteBuf.readShort();
    }
}
