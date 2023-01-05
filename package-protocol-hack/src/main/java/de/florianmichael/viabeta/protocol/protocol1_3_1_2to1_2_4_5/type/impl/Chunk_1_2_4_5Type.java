package de.florianmichael.viabeta.protocol.protocol1_3_1_2to1_2_4_5.type.impl;

import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.type.impl.Chunk_1_7_6_10Type;
import io.netty.buffer.ByteBuf;

public class Chunk_1_2_4_5Type extends Chunk_1_7_6_10Type {

    public Chunk_1_2_4_5Type(ClientWorld clientWorld) {
        super(clientWorld);
    }

    @Override
    protected void readUnusedInt(ByteBuf byteBuf, ClientWorld clientWorld) {
        byteBuf.readInt();
    }

    @Override
    protected void writeUnusedInt(ByteBuf byteBuf, ClientWorld clientWorld, Chunk chunk) {
        byteBuf.writeInt(0);
    }

}
