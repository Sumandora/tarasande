package de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.type.impl;

import com.viaversion.viaversion.api.type.Type;
import io.netty.buffer.ByteBuf;

public class IntArray_1_7_6_10Type extends Type<int[]> {

    public IntArray_1_7_6_10Type() {
        super(int[].class);
    }

    @Override
    public int[] read(ByteBuf buffer) throws Exception {
        final byte length = buffer.readByte();
        final int[] array = new int[length];

        for (byte i = 0; i < length; i++) {
            array[i] = buffer.readInt();
        }
        return array;
    }

    @Override
    public void write(ByteBuf buffer, int[] array) throws Exception {
        buffer.writeByte(array.length);
        for (int i : array) buffer.writeInt(i);
    }

}
