package de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.type.impl;

import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.opennbt.NBTIO;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import io.netty.buffer.*;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class NBT_1_7_6_10Type extends Type<CompoundTag> {

    private final boolean compressed;

    public NBT_1_7_6_10Type(final boolean compressed) {
        super(CompoundTag.class);
        this.compressed = compressed;
    }

    @Override
    public CompoundTag read(ByteBuf buffer) throws IOException {
        final short length = buffer.readShort();
        if (length <= 0) {
            return null;
        }

        final ByteBuf data = buffer.readSlice(length);
        try (InputStream in = this.compressed ? new GZIPInputStream(new ByteBufInputStream(data)) : new ByteBufInputStream(data)) {
            return NBTIO.readTag(in);
        }
    }

    @Override
    public void write(ByteBuf buffer, CompoundTag nbt) throws Exception {
        if (nbt == null) {
            buffer.writeShort(-1);
            return;
        }

        final ByteBuf data = buffer.alloc().buffer();
        try {
            try (OutputStream out = this.compressed ? new GZIPOutputStream(new ByteBufOutputStream(data)) : new ByteBufOutputStream(data)) {
                NBTIO.writeTag(out, nbt);
            }

            buffer.writeShort(data.readableBytes());
            buffer.writeBytes(data);
        } finally {
            data.release();
        }
    }

}
