package de.florianmichael.viacursed.netty.encoder;

import com.viaversion.viaversion.api.type.Type;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class NukkitPacketEncoder extends MessageToByteEncoder<ByteBuf> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) {
        System.out.println("Encoding");
        Type.VAR_INT.writePrimitive(out, Type.VAR_INT.readPrimitive(in)); // id
        in.writeBytes(out); // content
    }
}
