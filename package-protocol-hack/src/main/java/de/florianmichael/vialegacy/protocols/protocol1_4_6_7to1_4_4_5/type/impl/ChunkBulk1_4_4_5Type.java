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

package de.florianmichael.vialegacy.protocols.protocol1_4_6_7to1_4_4_5.type.impl;

import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.type.PartialType;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.CustomByteType;
import com.viaversion.viaversion.api.type.types.minecraft.BaseChunkBulkType;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;
import de.florianmichael.vialegacy.protocols.protocol1_8_0_9to1_7_6_10.type.impl.Chunk1_7_6_10Type;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class ChunkBulk1_4_4_5Type extends PartialType<Chunk[], ClientWorld> {

    private final boolean skyLight;

    public ChunkBulk1_4_4_5Type(final ClientWorld clientWorld, final boolean skyLight) {
        super(clientWorld, Chunk[].class);
        this.skyLight = skyLight;
    }

    @Override
    public Class<? extends Type> getBaseClass() {
        return BaseChunkBulkType.class;
    }

    @Override
    public Chunk[] read(ByteBuf input, ClientWorld world) throws Exception {
        final short columnCount = input.readShort();
        final int size = input.readInt();

        final int[] chunkX = new int[columnCount];
        final int[] chunkZ = new int[columnCount];
        final int[] primaryBitMask = new int[columnCount];
        final int[] addBitMask = new int[columnCount];
        final byte[][] inflatedBuffers = new byte[columnCount][];

        CustomByteType customByteType = new CustomByteType(size);
        byte[] buildBuffer = customByteType.read(input);

        byte[] data = new byte[196864 * columnCount];
        Inflater inflater = new Inflater();
        inflater.setInput(buildBuffer, 0, size);

        try {
            inflater.inflate(data);
        } catch (DataFormatException ex) {
            throw new IOException("Bad compressed data format");
        } finally {
            inflater.end();
        }

        int i = 0;

        for (int j = 0; j < columnCount; ++j) {
            chunkX[j] = input.readInt();
            chunkZ[j] = input.readInt();
            primaryBitMask[j] = input.readShort();
            addBitMask[j] = input.readShort();

            int k = 0;
            int l = 0;

            int i1;
            for (i1 = 0; i1 < 16; ++i1) {
                k += primaryBitMask[j] >> i1 & 1;
                l += addBitMask[j] >> i1 & 1;
            }

            i1 = 8192 * k + 256;
            i1 += 2048 * l;
            if (skyLight) {
                i1 += 2048 * k;
            }

            inflatedBuffers[j] = new byte[i1];
            System.arraycopy(data, i, inflatedBuffers[j], 0, i1);
            i += i1;
        }

        final Chunk[] chunks = new Chunk[columnCount];

        for (i = 0; i < columnCount; i++) {
            chunks[i] = Chunk1_7_6_10Type.deserialize(chunkX[i], chunkZ[i], true, skyLight, primaryBitMask[i], addBitMask[i], inflatedBuffers[i]);
        }

        return chunks;
    }

    @Override
    public void write(ByteBuf output, ClientWorld world, Chunk[] chunks) throws Exception {
        throw new UnsupportedOperationException();
    }
}
