package de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.type.impl;

import com.viaversion.viaversion.api.minecraft.Environment;
import com.viaversion.viaversion.api.minecraft.chunks.*;
import com.viaversion.viaversion.api.type.PartialType;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.CustomByteType;
import com.viaversion.viaversion.api.type.types.minecraft.BaseChunkType;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;
import com.viaversion.viaversion.util.Pair;
import de.florianmichael.viabeta.api.model.IdAndData;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.util.ExtendedBlockStorage_1_7_6_10;
import io.netty.buffer.ByteBuf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.*;

public class Chunk_1_7_6_10Type extends PartialType<Chunk, ClientWorld> {

    public Chunk_1_7_6_10Type(final ClientWorld clientWorld) {
        super(clientWorld, Chunk.class);
    }

    @Override
    public Class<? extends Type> getBaseClass() {
        return BaseChunkType.class;
    }

    /**
     * This method is here to allow overriding the code for 1.2.5 -{@literal >} 1.3.2 because it introduced an unused int
     *
     * @param byteBuf     The buffer
     * @param clientWorld The ClientWorld
     */
    protected void readUnusedInt(final ByteBuf byteBuf, final ClientWorld clientWorld) {
    }

    /**
     * This method is here to allow overriding the code for 1.2.5 -{@literal >} 1.3.2 because it introduced an unused int
     *
     * @param byteBuf     The buffer
     * @param clientWorld The ClientWorld
     * @param chunk       The Chunk
     */
    protected void writeUnusedInt(final ByteBuf byteBuf, final ClientWorld clientWorld, final Chunk chunk) {
    }

    @Override
    public Chunk read(ByteBuf byteBuf, ClientWorld clientWorld) throws Exception {
        final int chunkX = byteBuf.readInt();
        final int chunkZ = byteBuf.readInt();
        final boolean fullChunk = byteBuf.readBoolean();
        final short primaryBitMask = byteBuf.readShort();
        final short additionalBitMask = byteBuf.readShort();
        final int compressedSize = byteBuf.readInt();
        this.readUnusedInt(byteBuf, clientWorld);
        final byte[] data = new CustomByteType(compressedSize).read(byteBuf);

        final byte[] uncompressedData = new byte[getSize(primaryBitMask, additionalBitMask, fullChunk, clientWorld.getEnvironment() == Environment.NORMAL)];
        final Inflater inflater = new Inflater();
        try {
            inflater.setInput(data, 0, compressedSize);
            inflater.inflate(uncompressedData);
        } catch (DataFormatException ex) {
            throw new IOException("Bad compressed data format");
        } finally {
            inflater.end();
        }

        // Check if the chunk is an unload packet and return early
        if (fullChunk && primaryBitMask == 0) {
            return new BaseChunk(chunkX, chunkZ, true, false, 0, new ChunkSection[16], null, new ArrayList<>());
        }

        return deserialize(chunkX, chunkZ, fullChunk, clientWorld.getEnvironment() == Environment.NORMAL, primaryBitMask, additionalBitMask, uncompressedData);
    }

    @Override
    public void write(ByteBuf byteBuf, ClientWorld clientWorld, Chunk chunk) throws Exception {
        final Pair<byte[], Short> chunkData = serialize(chunk);
        final byte[] data = chunkData.key();
        final short additionalBitMask = chunkData.value();

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

        byteBuf.writeInt(chunk.getX());
        byteBuf.writeInt(chunk.getZ());
        byteBuf.writeBoolean(chunk.isFullChunk());
        byteBuf.writeShort(chunk.getBitmask());
        byteBuf.writeShort(additionalBitMask);
        byteBuf.writeInt(compressedSize);
        this.writeUnusedInt(byteBuf, clientWorld, chunk);
        byteBuf.writeBytes(compressedData, 0, compressedSize);
    }

    public static Chunk deserialize(final int chunkX, final int chunkZ, final boolean fullChunk, final boolean skyLight, final int primaryBitMask, final int additionalBitMask, final byte[] chunkData) {
        final ExtendedBlockStorage_1_7_6_10[] storageArrays = new ExtendedBlockStorage_1_7_6_10[16];

        int dataPosition = 0;
        for (int i = 0; i < storageArrays.length; i++) {
            if ((primaryBitMask & 1 << i) != 0) {
                if (storageArrays[i] == null) {
                    storageArrays[i] = new ExtendedBlockStorage_1_7_6_10(skyLight);
                }

                final byte[] lsbArray = storageArrays[i].getBlockLSBArray();
                System.arraycopy(chunkData, dataPosition, lsbArray, 0, lsbArray.length);
                dataPosition += lsbArray.length;
            }
        }

        for (int i = 0; i < storageArrays.length; i++) {
            if ((primaryBitMask & 1 << i) != 0 && storageArrays[i] != null) {
                final NibbleArray nibbleArray = storageArrays[i].getBlockMetadataArray();
                System.arraycopy(chunkData, dataPosition, nibbleArray.getHandle(), 0, nibbleArray.getHandle().length);
                dataPosition += nibbleArray.getHandle().length;
            }
        }

        for (int i = 0; i < storageArrays.length; i++) {
            if ((primaryBitMask & 1 << i) != 0 && storageArrays[i] != null) {
                final NibbleArray nibbleArray = storageArrays[i].getBlockLightArray();
                System.arraycopy(chunkData, dataPosition, nibbleArray.getHandle(), 0, nibbleArray.getHandle().length);
                dataPosition += nibbleArray.getHandle().length;
            }
        }

        if (skyLight) {
            for (int i = 0; i < storageArrays.length; i++) {
                if ((primaryBitMask & 1 << i) != 0 && storageArrays[i] != null) {
                    final NibbleArray nibbleArray = storageArrays[i].getSkyLightArray();
                    System.arraycopy(chunkData, dataPosition, nibbleArray.getHandle(), 0, nibbleArray.getHandle().length);
                    dataPosition += nibbleArray.getHandle().length;
                }
            }
        }

        for (int i = 0; i < storageArrays.length; i++) {
            if ((additionalBitMask & 1 << i) != 0) {
                if (storageArrays[i] != null) {
                    final NibbleArray nibbleArray = storageArrays[i].getOrCreateBlockMSBArray();
                    System.arraycopy(chunkData, dataPosition, nibbleArray.getHandle(), 0, nibbleArray.getHandle().length);
                    dataPosition += nibbleArray.getHandle().length;
                } else {
                    dataPosition += 2048;
                }
            }
        }

        int[] biomeData = null;
        if (fullChunk) {
            biomeData = new int[256];
            for (int i = 0; i < biomeData.length; i++) {
                biomeData[i] = chunkData[dataPosition + i] & 255;
            }
            dataPosition += biomeData.length;
        }

        final ChunkSection[] sections = new ChunkSection[16];
        for (int i = 0; i < storageArrays.length; i++) {
            final ExtendedBlockStorage_1_7_6_10 storage = storageArrays[i];
            if (storage != null) {
                final ChunkSection section = sections[i] = new ChunkSectionImpl(true);
                section.palette(PaletteType.BLOCKS).addId(0);
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        for (int y = 0; y < 16; y++) {
                            section.palette(PaletteType.BLOCKS).setIdAt(x, y, z, IdAndData.toCompressedData(storage.getBlockId(x, y, z), storage.getBlockMetadata(x, y, z)));
                        }
                    }
                }
                section.getLight().setBlockLight(storage.getBlockLightArray().getHandle());
                if (skyLight) {
                    section.getLight().setSkyLight(storage.getSkyLightArray().getHandle());
                }
            }
        }

        return new BaseChunk(chunkX, chunkZ, fullChunk, false, primaryBitMask, sections, biomeData, new ArrayList<>());
    }

    public static Pair<byte[], Short> serialize(final Chunk chunk) throws IOException {
        final ExtendedBlockStorage_1_7_6_10[] storageArrays = new ExtendedBlockStorage_1_7_6_10[16];
        for (int i = 0; i < storageArrays.length; i++) {
            final ChunkSection section = chunk.getSections()[i];
            if (section != null) {
                final ExtendedBlockStorage_1_7_6_10 storage = storageArrays[i] = new ExtendedBlockStorage_1_7_6_10(section.getLight().hasSkyLight());
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        for (int y = 0; y < 16; y++) {
                            final int flatBlock = section.palette(PaletteType.BLOCKS).idAt(x, y, z);
                            storage.setBlockId(x, y, z, flatBlock >> 4);
                            storage.setBlockMetadata(x, y, z, flatBlock & 15);
                        }
                    }
                }
                storage.getBlockLightArray().setHandle(section.getLight().getBlockLight());
                if (section.getLight().hasSkyLight()) {
                    storage.getSkyLightArray().setHandle(section.getLight().getSkyLight());
                }
            }
        }

        final ByteArrayOutputStream output = new ByteArrayOutputStream();

        for (int i = 0; i < storageArrays.length; i++) {
            if ((chunk.getBitmask() & 1 << i) != 0) {
                output.write(storageArrays[i].getBlockLSBArray());
            }
        }

        for (int i = 0; i < storageArrays.length; i++) {
            if ((chunk.getBitmask() & 1 << i) != 0) {
                output.write(storageArrays[i].getBlockMetadataArray().getHandle());
            }
        }

        for (int i = 0; i < storageArrays.length; i++) {
            if ((chunk.getBitmask() & 1 << i) != 0) {
                output.write(storageArrays[i].getBlockLightArray().getHandle());
            }
        }

        for (int i = 0; i < storageArrays.length; i++) {
            if ((chunk.getBitmask() & 1 << i) != 0 && storageArrays[i].getSkyLightArray() != null) {
                output.write(storageArrays[i].getSkyLightArray().getHandle());
            }
        }

        short additionalBitMask = 0;
        for (int i = 0; i < storageArrays.length; i++) {
            if ((chunk.getBitmask() & 1 << i) != 0 && storageArrays[i].hasBlockMSBArray()) {
                additionalBitMask |= 1 << i;
                output.write(storageArrays[i].getOrCreateBlockMSBArray().getHandle());
            }
        }

        if (chunk.isFullChunk() && chunk.getBiomeData() != null) {
            for (int biome : chunk.getBiomeData()) {
                output.write(biome);
            }
        }

        return new Pair<>(output.toByteArray(), additionalBitMask);
    }

    public static int getSize(final short primaryBitMask, final short additionalBitMask, final boolean fullChunk, final boolean skyLight) {
        final int primarySectionCount = Integer.bitCount(primaryBitMask & 0xFFFF);
        final int additionalSectionCount = Integer.bitCount(additionalBitMask & 0xFFFF);

        int size = ((4096 + 2048 + 2048) * primarySectionCount) + (2048 * additionalSectionCount);
        if (skyLight) size += 2048 * primarySectionCount;
        if (fullChunk) size += 256;

        return size;
    }

}
