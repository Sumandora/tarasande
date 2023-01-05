package de.florianmichael.viabeta.protocol.classic.protocola1_0_15toc0_28_30.type.impl;

import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.type.Type;
import io.netty.buffer.ByteBuf;

public class Position_c0_30Type extends Type<Position> {

    public Position_c0_30Type() {
        super(Position.class);
    }

    @Override
    public Position read(ByteBuf buffer) throws Exception {
        return new Position(buffer.readShort(), (int) buffer.readShort(), buffer.readShort());
    }

    @Override
    public void write(ByteBuf buffer, Position position) throws Exception {
        buffer.writeShort(position.x());
        buffer.writeShort(position.y());
        buffer.writeShort(position.z());
    }

}
