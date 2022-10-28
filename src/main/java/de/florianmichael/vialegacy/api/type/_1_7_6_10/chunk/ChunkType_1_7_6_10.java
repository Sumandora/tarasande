package de.florianmichael.vialegacy.api.type._1_7_6_10.chunk;

import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.vialegacy.protocols.protocol1_8to1_7_10.chunk.Chunk1_8to1_7_6_10;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class ChunkType_1_7_6_10 extends Type<Chunk> {

    public ChunkType_1_7_6_10() {
        super(Chunk.class);
    }

    @Override
    public Chunk read(ByteBuf buffer) throws Exception {
        int chunkX = buffer.readInt();
        int chunkZ = buffer.readInt();
        boolean groundUp = buffer.readBoolean();
        int primaryBitMask = buffer.readShort();
        int addBitMask = buffer.readShort();
        int compressedSize = buffer.readInt();
        byte[] data = new byte[compressedSize];
        buffer.readBytes(data);

        int k = 0;
        int l = 0;

        for (int j = 0; j < 16; ++j) {
            k += primaryBitMask >> j & 1;
            l += addBitMask >> j & 1;
        }

        int uncompressedSize = 12288 * k;
        uncompressedSize += 2048 * l;
        if (groundUp) {
            uncompressedSize += 256;
        }

        byte[] uncompressedData = new byte[uncompressedSize];
        Inflater inflater = new Inflater();
        inflater.setInput(data, 0, compressedSize);
        try {
            inflater.inflate(uncompressedData);
        } catch (DataFormatException ex) {
            throw new IOException("Bad compressed data format");
        } finally {
            inflater.end();
        }

        return new Chunk1_8to1_7_6_10(chunkX, chunkZ, uncompressedData, primaryBitMask, addBitMask, true, groundUp);
    }

    @Override
    public void write(ByteBuf buffer, Chunk object) throws Exception {
        buffer.writeInt(object.getX());
        buffer.writeInt(object.getZ());
        buffer.writeBoolean(object.isFullChunk());
        buffer.writeShort(object.getBitmask());
        byte[] data1_8 = ((Chunk1_8to1_7_6_10) object).get1_8Data();
        Type.VAR_INT.writePrimitive(buffer, data1_8.length);
        buffer.writeBytes(data1_8);
    }
}
