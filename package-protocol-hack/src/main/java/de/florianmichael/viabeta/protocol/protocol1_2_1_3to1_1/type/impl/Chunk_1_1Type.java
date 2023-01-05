package de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.type.impl;

import com.viaversion.viaversion.api.minecraft.Environment;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.minecraft.chunks.*;
import com.viaversion.viaversion.api.type.PartialType;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.CustomByteType;
import com.viaversion.viaversion.api.type.types.minecraft.BaseChunkType;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;
import de.florianmichael.viabeta.api.model.IdAndData;
import de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.model.NibbleArray1_1;
import de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.model.NonFullChunk1_1;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class Chunk_1_1Type extends PartialType<Chunk, ClientWorld> {

    public Chunk_1_1Type(ClientWorld clientWorld) {
        super(clientWorld, Chunk.class);
    }

    @Override
    public Class<? extends Type> getBaseClass() {
        return BaseChunkType.class;
    }

    @Override
    public Chunk read(ByteBuf byteBuf, ClientWorld clientWorld) throws Exception {
        final int xPosition = byteBuf.readInt();
        final int yPosition = byteBuf.readShort();
        final int zPosition = byteBuf.readInt();
        final int xSize = byteBuf.readUnsignedByte() + 1;
        final int ySize = byteBuf.readUnsignedByte() + 1;
        final int zSize = byteBuf.readUnsignedByte() + 1;
        final int chunkSize = byteBuf.readInt();
        final byte[] compressedData = new CustomByteType(chunkSize).read(byteBuf);
        final byte[] uncompressedData = new byte[(xSize * ySize * zSize * 5) / 2];
        final Inflater inflater = new Inflater();
        inflater.setInput(compressedData);
        try {
            inflater.inflate(uncompressedData);
        } catch (DataFormatException dataformatexception) {
            throw new IOException("Bad compressed data format");
        } finally {
            inflater.end();
        }

        return deserialize(xPosition, yPosition, zPosition, xSize, ySize, zSize, clientWorld.getEnvironment() == Environment.NORMAL, uncompressedData);
    }

    @Override
    public void write(ByteBuf byteBuf, ClientWorld clientWorld, Chunk chunk) throws Exception {
        throw new UnsupportedOperationException();
    }

    public static Chunk deserialize(final int xPosition, final int yPosition, final int zPosition, final int xSize, final int ySize, final int zSize, final boolean skyLight, final byte[] chunkData) {
        final int chunkX = xPosition >> 4;
        final int chunkZ = zPosition >> 4;
        final int endChunkX = xPosition + xSize - 1 >> 4;
        final int endChunkZ = zPosition + zSize - 1 >> 4;
        final int startX = Math.max(xPosition - chunkX * 16, 0);
        final int endX = Math.min(xPosition + xSize - chunkX * 16, 16);
        final int startY = Math.max(yPosition, 0);
        final int endY = Math.min(yPosition + ySize, 128);
        final int startZ = Math.max(zPosition - chunkZ * 16, 0);
        final int endZ = Math.min(zPosition + zSize - chunkZ * 16, 16);
        final boolean fullChunk = (xPosition & 15) == 0 && yPosition == 0 && (zPosition & 15) == 0 && xSize == 16 && ySize == 128 && zSize == 16;

        final byte[] blockArray = new byte[16 * 128 * 16];
        final NibbleArray1_1 blockDataArray = new NibbleArray1_1(blockArray.length);
        final NibbleArray1_1 blockLightArray = new NibbleArray1_1(blockArray.length);
        final NibbleArray1_1 skyLightArray = new NibbleArray1_1(blockArray.length);

        if (fullChunk) { // fast path for most common chunk size
            final int blockDataOffset = blockArray.length;
            final int blockLightOffset = blockArray.length + blockDataArray.getHandle().length;
            final int skyLightOffset = chunkData.length - blockLightArray.getHandle().length;

            System.arraycopy(chunkData, 0, blockArray, 0, blockArray.length);
            System.arraycopy(chunkData, blockDataOffset, blockDataArray.getHandle(), 0, blockDataArray.getHandle().length);
            System.arraycopy(chunkData, blockLightOffset, blockLightArray.getHandle(), 0, blockLightArray.getHandle().length);
            System.arraycopy(chunkData, skyLightOffset, skyLightArray.getHandle(), 0, skyLightArray.getHandle().length);
        } else {
            if (chunkX != endChunkX || chunkZ != endChunkZ) throw new IllegalStateException("Can't decode 1.1 non full-chunk which spans over multiple chunks");

            int offset = 0;
            for (int x = startX; x < endX; x++) {
                for (int z = startZ; z < endZ; z++) {
                    final int index = x << 11 | z << 7 | startY;
                    final int size = endY - startY;
                    System.arraycopy(chunkData, offset, blockArray, index, size);
                    offset += size;
                }
            }
            for (int x = startX; x < endX; x++) {
                for (int z = startZ; z < endZ; z++) {
                    final int index = (x << 11 | z << 7 | startY) >> 1;
                    final int size = (endY - startY) / 2;
                    System.arraycopy(chunkData, offset, blockDataArray.getHandle(), index, size);
                    offset += size;
                }
            }
            for (int x = startX; x < endX; x++) {
                for (int z = startZ; z < endZ; z++) {
                    final int index = (x << 11 | z << 7 | startY) >> 1;
                    final int size = (endY - startY) / 2;
                    System.arraycopy(chunkData, offset, blockLightArray.getHandle(), index, size);
                    offset += size;
                }
            }
            for (int x = startX; x < endX; x++) {
                for (int z = startZ; z < endZ; z++) {
                    final int index = (x << 11 | z << 7 | startY) >> 1;
                    final int size = (endY - startY) / 2;
                    System.arraycopy(chunkData, offset, skyLightArray.getHandle(), index, size);
                    offset += size;
                }
            }
        }

        final ChunkSection[] modernSections = new ChunkSection[8];

        int bitmask = 0;
        ChunkSection section = null;
        NibbleArray1_1 sectionSkyLight = null;
        NibbleArray1_1 sectionBlockLight = null;
        for (int y = startY; y < endY; y++) {
            if (section == null || y % 16 == 0) {
                final int sectionY = y >> 4;
                bitmask |= (1 << sectionY);
                section = modernSections[sectionY] = new ChunkSectionImpl(true);
                section.palette(PaletteType.BLOCKS).addId(0);
                sectionSkyLight = new NibbleArray1_1(16 * 16 * 16, 4);
                sectionBlockLight = new NibbleArray1_1(16 * 16 * 16, 4);
            }

            for (int x = startX; x < endX; x++) {
                for (int z = startZ; z < endZ; z++) {
                    section.palette(PaletteType.BLOCKS).setIdAt(x, y & 15, z, IdAndData.toCompressedData(blockArray[x << 11 | z << 7 | y] & 255, blockDataArray.get(x, y, z)));
                    sectionSkyLight.set(x, y & 15, z, skyLightArray.get(x, y, z));
                    sectionBlockLight.set(x, y & 15, z, blockLightArray.get(x, y, z));
                }
            }

            section.getLight().setBlockLight(sectionBlockLight.getHandle());
            if (skyLight) {
                section.getLight().setSkyLight(sectionSkyLight.getHandle());
            }
        }

        if (fullChunk) {
            return new BaseChunk(chunkX, chunkZ, true, false, 255, modernSections, new int[256], new ArrayList<>());
        } else {
            return new NonFullChunk1_1(chunkX, chunkZ, bitmask, modernSections, new Position(startX, startY, startZ), new Position(endX, endY, endZ));
        }
    }

}
