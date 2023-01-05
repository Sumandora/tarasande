package de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.type.impl;

import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.type.PartialType;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.CustomByteType;
import com.viaversion.viaversion.api.type.types.minecraft.BaseChunkBulkType;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;
import com.viaversion.viaversion.util.Pair;
import io.netty.buffer.ByteBuf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.*;

public class ChunkBulk_1_7_6_10Type extends PartialType<Chunk[], ClientWorld> {

    public ChunkBulk_1_7_6_10Type(final ClientWorld clientWorld) {
        super(clientWorld, Chunk[].class);
    }

    @Override
    public Class<? extends Type> getBaseClass() {
        return BaseChunkBulkType.class;
    }

    /**
     * This method is here to allow overriding the code for 1.4.5 -{@literal >} 1.4.7
     *
     * @param byteBuf     The buffer
     * @param clientWorld The ClientWorld
     * @return Read skylight array or not
     */
    protected boolean readHasSkyLight(final ByteBuf byteBuf, final ClientWorld clientWorld) {
        return byteBuf.readBoolean();
    }

    /**
     * This method is here to allow overriding the code for 1.4.5 -{@literal >} 1.4.7
     *
     * @param byteBuf     The buffer
     * @param clientWorld The ClientWorld
     * @param hasSkyLight Has skylight
     */
    protected void writeHasSkyLight(final ByteBuf byteBuf, final ClientWorld clientWorld, final boolean hasSkyLight) {
        byteBuf.writeBoolean(hasSkyLight);
    }

    @Override
    public Chunk[] read(ByteBuf byteBuf, ClientWorld clientWorld) throws Exception {
        final short chunkCount = byteBuf.readShort();
        final int compressedSize = byteBuf.readInt();
        final boolean hasSkyLight = this.readHasSkyLight(byteBuf, clientWorld);
        final byte[] data = new CustomByteType(compressedSize).read(byteBuf);
        final int[] chunkX = new int[chunkCount];
        final int[] chunkZ = new int[chunkCount];
        final short[] primaryBitMask = new short[chunkCount];
        final short[] additionalBitMask = new short[chunkCount];
        for (int i = 0; i < chunkCount; i++) {
            chunkX[i] = byteBuf.readInt();
            chunkZ[i] = byteBuf.readInt();
            primaryBitMask[i] = byteBuf.readShort();
            additionalBitMask[i] = byteBuf.readShort();
        }

        final byte[] uncompressedData = new byte[Chunk_1_7_6_10Type.getSize((short) 0xFFFF, (short) 0xFFFF, true, hasSkyLight) * chunkCount];
        final Inflater inflater = new Inflater();
        try {
            inflater.setInput(data, 0, compressedSize);
            inflater.inflate(uncompressedData);
        } catch (DataFormatException ex) {
            throw new IOException("Bad compressed data format");
        } finally {
            inflater.end();
        }

        final Chunk[] chunks = new Chunk[chunkCount];
        int dataPosition = 0;
        for (int i = 0; i < chunkCount; i++) {
            final byte[] chunkData = new byte[Chunk_1_7_6_10Type.getSize(primaryBitMask[i], additionalBitMask[i], true, hasSkyLight)];
            System.arraycopy(uncompressedData, dataPosition, chunkData, 0, chunkData.length);
            chunks[i] = Chunk_1_7_6_10Type.deserialize(chunkX[i], chunkZ[i], true, hasSkyLight, primaryBitMask[i], additionalBitMask[i], chunkData);
            dataPosition += chunkData.length;
        }

        return chunks;
    }

    @Override
    public void write(ByteBuf byteBuf, ClientWorld clientWorld, Chunk[] chunks) throws Exception {
        final int chunkCount = chunks.length;
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final int[] chunkX = new int[chunkCount];
        final int[] chunkZ = new int[chunkCount];
        final short[] primaryBitMask = new short[chunkCount];
        final short[] additionalBitMask = new short[chunkCount];

        for (int i = 0; i < chunkCount; i++) {
            final Chunk chunk = chunks[i];
            final Pair<byte[], Short> chunkData = Chunk_1_7_6_10Type.serialize(chunk);
            output.write(chunkData.key());
            chunkX[i] = chunk.getX();
            chunkZ[i] = chunk.getZ();
            primaryBitMask[i] = (short) chunk.getBitmask();
            additionalBitMask[i] = chunkData.value();
        }
        final byte[] data = output.toByteArray();

        final Deflater deflater = new Deflater();
        byte[] compressedData;
        int compressedSize;
        try {
            deflater.setInput(data, 0, data.length);
            deflater.finish();
            compressedData = new byte[data.length];
            compressedSize = deflater.deflate(compressedData);
        } finally {
            deflater.end();
        }

        byteBuf.writeShort(chunkCount);
        byteBuf.writeInt(compressedSize);
        this.writeHasSkyLight(byteBuf, clientWorld, true);
        byteBuf.writeBytes(compressedData, 0, compressedSize);

        for (int i = 0; i < chunkCount; i++) {
            byteBuf.writeInt(chunkX[i]);
            byteBuf.writeInt(chunkZ[i]);
            byteBuf.writeShort(primaryBitMask[i]);
            byteBuf.writeShort(additionalBitMask[i]);
        }
    }

}
