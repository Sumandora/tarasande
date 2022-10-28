package de.florianmichael.vialegacy.api.type._1_7_6_10.chunk;

import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.vialegacy.protocols.protocol1_8to1_7_10.chunk.Chunk1_8to1_7_6_10;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class ChunkBulkType_1_7_6_10 extends Type<Chunk[]> {
    public ChunkBulkType_1_7_6_10() {
        super(Chunk[].class);
    }

    @Override
    public Chunk[] read(ByteBuf buffer) throws Exception {
        short chunkColumns = buffer.readShort();
        int dataLength = buffer.readInt();
        boolean skyLight = buffer.readBoolean();
        byte[] chunkData = new byte[dataLength];
        buffer.readBytes(chunkData);

        byte[] data = new byte[196864 * chunkColumns]; // abyte
        Inflater inflater = new Inflater();
        inflater.setInput(chunkData, 0, dataLength);

        try {
            inflater.inflate(data);
        } catch (DataFormatException ex) {
            throw new IOException("Bad compressed data format");
        } finally {
            inflater.end();
        }

        int i = 0;

        Chunk1_8to1_7_6_10[] chunks = new Chunk1_8to1_7_6_10[chunkColumns];

        for (int j = 0; j < chunkColumns; j++) {
            int chunkX = buffer.readInt();
            int chunkZ = buffer.readInt();
            int primaryBitMask = buffer.readShort();
            int addBitMask = buffer.readShort();
            int k = 0;
            int l = 0;

            int i1;
            for (i1 = 0; i1 < 16; ++i1) {
                k += primaryBitMask >> i1 & 1;
                l += addBitMask >> i1 & 1;
            }

            i1 = 8192 * k + 256;
            i1 += 2048 * l;
            if (skyLight) {
                i1 += 2048 * k;
            }

            byte[] inflatedBuffers = new byte[i1];
            System.arraycopy(data, i, inflatedBuffers, 0, i1);
            i += i1;
            chunks[j] = new Chunk1_8to1_7_6_10(chunkX, chunkZ, inflatedBuffers, primaryBitMask, addBitMask, skyLight, true);
        }

        return chunks;
    }

    @Override
    public void write(ByteBuf buffer, Chunk[] value) throws Exception {
        buffer.writeBoolean(value[0].isIgnoreOldLightData()); // skyLight is the same among all chunks, just take it from the first here
        Type.VAR_INT.writePrimitive(buffer, value.length);
        for (Chunk chunk : value) {
            buffer.writeInt(chunk.getX());
            buffer.writeInt(chunk.getZ());
            buffer.writeShort(chunk.getBitmask());
        }
        for (Chunk chunk : value) {
            buffer.writeBytes(((Chunk1_8to1_7_6_10) chunk).get1_8Data());
        }
    }
}
