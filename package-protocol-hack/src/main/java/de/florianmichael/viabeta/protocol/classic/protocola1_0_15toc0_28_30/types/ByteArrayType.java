package de.florianmichael.viabeta.protocol.classic.protocola1_0_15toc0_28_30.types;

import com.viaversion.viaversion.api.type.Type;
import io.netty.buffer.ByteBuf;

public class ByteArrayType extends Type<byte[]> {

    public ByteArrayType() {
        super(byte[].class);
    }

    public void write(ByteBuf buffer, byte[] array) throws Exception {
        if (array.length != 1024) throw new IllegalStateException("Byte array needs to be exactly 1024 bytes long");

        buffer.writeBytes(array);
    }

    public byte[] read(ByteBuf buffer) throws Exception {
        final byte[] array = new byte[1024];
        buffer.readBytes(array);
        return array;
    }

}
