package de.florianmichael.viabeta.protocol.classic.protocola1_0_15toc0_28_30.types;

import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.viabeta.protocol.classic.protocola1_0_15toc0_28_30.data.Cp437String;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.Arrays;

public class StringType extends Type<String> {

    public StringType() {
        super(String.class);
    }

    public String read(ByteBuf buffer) throws IOException {
        final byte[] stringBytes = new byte[64];
        buffer.readBytes(stringBytes);
        return Cp437String.fromBytes(stringBytes).trim();
    }

    public void write(ByteBuf buffer, String s) throws IOException {
        final byte[] bytes = new byte[64];
        final byte[] stringBytes = Cp437String.toBytes(s);

        Arrays.fill(bytes, (byte) 32);
        System.arraycopy(stringBytes, 0, bytes, 0, Math.min(bytes.length, stringBytes.length));

        buffer.writeBytes(bytes);
    }

}
