package de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.type.impl;

import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.type.Type;
import io.netty.buffer.ByteBuf;

import java.util.function.IntFunction;

public class PositionVarY_1_7_6_10Type<T extends Number> extends Type<Position> {

    private final Type<T> yType;
    private final IntFunction<T> yConverter;

    public PositionVarY_1_7_6_10Type(final Type<T> yType, final IntFunction<T> yConverter) {
        super(Position.class);
        this.yType = yType;
        this.yConverter = yConverter;
    }

    @Override
    public Position read(ByteBuf buffer) throws Exception {
        return new Position(buffer.readInt(), this.yType.read(buffer).intValue(), buffer.readInt());
    }

    @Override
    public void write(ByteBuf buffer, Position position) throws Exception {
        buffer.writeInt(position.x());
        this.yType.write(buffer, this.yConverter.apply(position.y()));
        buffer.writeInt(position.z());
    }

}
