package de.florianmichael.vialegacy.protocols.protocol1_3_1_2to1_2_4_5.type.impl;

import com.viaversion.viaversion.api.minecraft.Environment;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.type.PartialType;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;
import de.florianmichael.vialegacy.protocols.protocol1_8_0_9to1_7_6_10.type.impl.Chunk1_7_6_10Type;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class Chunk1_2_5Type extends PartialType<Chunk, ClientWorld> {

    public Chunk1_2_5Type(ClientWorld param) {
        super(param, Chunk.class);
    }

    @Override
    public Chunk read(ByteBuf byteBuf, ClientWorld clientWorld) throws Exception {
        final int chunkX = byteBuf.readInt();
        final int chunkZ = byteBuf.readInt();
        final boolean groundUp = byteBuf.readBoolean();
        final int primaryBitMask = byteBuf.readShort();
        final int addBitMask = byteBuf.readShort();
        final int compressedSize = byteBuf.readInt();
        byteBuf.readInt(); // Doesn't seem to be used by the client. Always 0. I expect this is Mod API stuff.
        final byte[] data = new byte[compressedSize];
        byteBuf.readBytes(data);

        final byte[] uncompressedData = new byte[Chunk1_7_6_10Type.calculateUncompressedSize(primaryBitMask, addBitMask, groundUp)];
        Inflater inflater = new Inflater();
        inflater.setInput(data, 0, compressedSize);
        try {
            inflater.inflate(uncompressedData);
        } catch (DataFormatException ex) {
            throw new IOException("Bad compressed data format");
        } finally {
            inflater.end();
        }

        return Chunk1_7_6_10Type.deserialize(chunkX, chunkZ, groundUp, clientWorld.getEnvironment() == Environment.NORMAL, primaryBitMask, addBitMask, uncompressedData);
    }

    @Override
    public void write(ByteBuf buffer, ClientWorld param, Chunk object) throws Exception {
        throw new UnsupportedOperationException();
    }
}
