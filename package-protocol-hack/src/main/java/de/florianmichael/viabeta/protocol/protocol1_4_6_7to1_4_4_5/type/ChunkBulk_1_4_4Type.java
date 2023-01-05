package de.florianmichael.viabeta.protocol.protocol1_4_6_7to1_4_4_5.type;

import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;
import io.netty.buffer.ByteBuf;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.type.impl.ChunkBulk_1_7_6_10Type;

public class ChunkBulk_1_4_4Type extends ChunkBulk_1_7_6_10Type {

    public ChunkBulk_1_4_4Type(ClientWorld clientWorld) {
        super(clientWorld);
    }

    @Override
    protected boolean readHasSkyLight(ByteBuf byteBuf, ClientWorld clientWorld) {
        return true;
    }

    @Override
    protected void writeHasSkyLight(ByteBuf byteBuf, ClientWorld clientWorld, boolean hasSkyLight) {
    }

}
