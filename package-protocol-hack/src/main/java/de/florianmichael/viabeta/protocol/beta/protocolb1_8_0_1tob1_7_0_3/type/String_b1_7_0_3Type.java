package de.florianmichael.viabeta.protocol.beta.protocolb1_8_0_1tob1_7_0_3.type;

import com.viaversion.viaversion.api.type.Type;
import io.netty.buffer.*;

import java.io.IOException;

public class String_b1_7_0_3Type extends Type<String> {

    public String_b1_7_0_3Type() {
        super(String.class);
    }

    public String read(ByteBuf buffer) throws IOException {
        final ByteBufInputStream dis = new ByteBufInputStream(buffer);
        return dis.readUTF();
    }

    public void write(ByteBuf buffer, String s) throws IOException {
        final ByteBufOutputStream dos = new ByteBufOutputStream(buffer);
        dos.writeUTF(s);
    }

}
