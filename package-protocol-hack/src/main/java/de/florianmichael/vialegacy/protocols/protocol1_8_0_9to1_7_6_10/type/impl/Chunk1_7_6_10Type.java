/**
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.2--
 *
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license. The creator assumes no responsibility for any infringements
 * that have arisen, are arising or will arise from this project / file. If this licence is used anywhere,
 * the latest version published by the author Florian Michael (aka EnZaXD) always applies automatically.
 *
 * Changelog:
 *     v1.0:
 *         Added License
 *     v1.1:
 *         Ownership withdrawn
 *     v1.2:
 *         Version-independent validity and automatic renewal
 */

package de.florianmichael.vialegacy.protocols.protocol1_8_0_9to1_7_6_10.type.impl;

import com.viaversion.viaversion.api.minecraft.Environment;
import com.viaversion.viaversion.api.minecraft.chunks.*;
import com.viaversion.viaversion.api.type.PartialType;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;
import de.florianmichael.vialegacy.protocols.protocol1_8_0_9to1_7_6_10.chunk.ExtendedBlockStorage;
import de.florianmichael.vialegacy.protocols.protocol1_8_0_9to1_7_6_10.chunk.SpigotDataFixer;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class Chunk1_7_6_10Type extends PartialType<Chunk, ClientWorld> {

    public Chunk1_7_6_10Type(ClientWorld param) {
        super(param, Chunk.class);
    }

    @Override
    public Chunk read(ByteBuf byteBuf, ClientWorld clientWorld) throws Exception {
        int chunkX = byteBuf.readInt();
        int chunkZ = byteBuf.readInt();
        boolean groundUp = byteBuf.readBoolean();
        int primaryBitMask = byteBuf.readShort();
        int addBitMask = byteBuf.readShort();
        int compressedSize = byteBuf.readInt();
        byte[] data = new byte[compressedSize];
        byteBuf.readBytes(data);

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

        return deserialize(chunkX, chunkZ, groundUp, clientWorld.getEnvironment() == Environment.NORMAL, primaryBitMask, addBitMask, uncompressedData);
    }

    public static Chunk deserialize(final int chunkX, final int chunkZ, final boolean groundUp, final boolean skyLight, final int primaryBitMask, final int addBitMask, final byte[] data) {
        final ExtendedBlockStorage[] storageArrays = new ExtendedBlockStorage[16];
        final ChunkSection[] sections = new ChunkSection[16];
        final byte[] blockBiomeArray = new byte[256];

        int dataSize = 0;
        for (int i = 0; i < storageArrays.length; i++) {
            if ((primaryBitMask & 1 << i) != 0) {
                if (storageArrays[i] == null) {
                    storageArrays[i] = new ExtendedBlockStorage(skyLight);
                    sections[i] = new ChunkSectionImpl(skyLight);
                }
                byte[] blockIds = storageArrays[i].getBlockLSBArray();
                System.arraycopy(data, dataSize, blockIds, 0, blockIds.length);
                dataSize += blockIds.length;
            } else if (storageArrays[i] != null && groundUp) {
                storageArrays[i] = null;
            }
        }
        for (int i = 0; i < storageArrays.length; i++) {
            if ((primaryBitMask & 1 << i) != 0 && storageArrays[i] != null) {
                NibbleArray nibblearray = storageArrays[i].getMetadataArray();
                System.arraycopy(data, dataSize, nibblearray.getHandle(), 0, nibblearray.getHandle().length);
                dataSize += nibblearray.getHandle().length;
            }
        }
        for (int i = 0; i < storageArrays.length; i++) {
            if ((primaryBitMask & 1 << i) != 0 && storageArrays[i] != null) {
                NibbleArray nibblearray = storageArrays[i].getBlocklightArray();
                System.arraycopy(data, dataSize, nibblearray.getHandle(), 0, nibblearray.getHandle().length);
                dataSize += nibblearray.getHandle().length;
            }
        }
        if (skyLight) {
            for (int i = 0; i < storageArrays.length; i++) {
                if ((primaryBitMask & 1 << i) != 0 && storageArrays[i] != null) {
                    NibbleArray nibblearray = storageArrays[i].getSkylightArray();
                    System.arraycopy(data, dataSize, nibblearray.getHandle(), 0, nibblearray.getHandle().length);
                    dataSize += nibblearray.getHandle().length;
                }
            }
        }
        for (int i = 0; i < storageArrays.length; i++) {
            if ((addBitMask & 1 << i) != 0) {
                if (storageArrays[i] == null) {
                    dataSize += 2048;
                } else {
                    NibbleArray nibblearray = storageArrays[i].getBlockMSBArray();
                    if (nibblearray == null) {
                        nibblearray = storageArrays[i].createBlockMSBArray();
                    }
                    System.arraycopy(data, dataSize, nibblearray.getHandle(), 0, nibblearray.getHandle().length);
                    dataSize += nibblearray.getHandle().length;
                }
            } else if (groundUp && storageArrays[i] != null && storageArrays[i].getBlockMSBArray() != null) {
                storageArrays[i].clearMSBArray();
            }
        }
        if (groundUp) {
            System.arraycopy(data, dataSize, blockBiomeArray, 0, blockBiomeArray.length);
        }

        for (int i = 0; i < storageArrays.length; ++i) {
            if (storageArrays[i] != null && (primaryBitMask & 1 << i) != 0 && (!groundUp || storageArrays[i].isEmpty())) {
                byte[] blockIds = storageArrays[i].getBlockLSBArray();
                NibbleArray nibblearray = storageArrays[i].getMetadataArray();

                for (int ind = 0; ind < blockIds.length; ++ind) {
                    int id = blockIds[ind] & 255;
                    int px = ind & 15;
                    int py = ind >> 8 & 15;
                    int pz = ind >> 4 & 15;
                    int blockData = nibblearray.get(px, py, pz);

                    blockData = SpigotDataFixer.getCorrectedData(id, blockData);

                    char val = (char) (id << 4 | blockData);
                    sections[i].palette(PaletteType.BLOCKS).setIdAt(px, py, pz, val);
                }
            }
        }

        for (int i = 0; i < storageArrays.length; ++i) {
            if (storageArrays[i] != null && (primaryBitMask & 1 << i) != 0 && (!groundUp || storageArrays[i].isEmpty())) {
                NibbleArray nibblearray = storageArrays[i].getBlocklightArray();
                sections[i].getLight().setBlockLight(nibblearray.getHandle());
            }
        }

        if (skyLight) {
            for (int i = 0; i < storageArrays.length; ++i) {
                if (storageArrays[i] != null && (primaryBitMask & 1 << i) != 0 && (!groundUp || storageArrays[i].isEmpty())) {
                    NibbleArray nibblearray = storageArrays[i].getSkylightArray();
                    sections[i].getLight().setSkyLight(nibblearray.getHandle());
                }
            }
        }

        final int[] biomeData = new int[blockBiomeArray.length];

        for (int i = 0; i < blockBiomeArray.length; i++) {
            biomeData[i] = blockBiomeArray[i];
        }

        return new BaseChunk(chunkX, chunkZ, groundUp, false, primaryBitMask, sections, biomeData, new ArrayList<>());
    }

    @Override
    public void write(ByteBuf output, ClientWorld clientWorld, Chunk chunk) throws Exception {
        throw new UnsupportedOperationException();
    }
}
