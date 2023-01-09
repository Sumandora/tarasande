package de.florianmichael.viacursed.netty.decoder;

import com.viaversion.viaversion.api.type.Type;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class NukkitPacketDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        System.out.println("Decoding packet with id: " + Type.VAR_INT.readPrimitive(in.copy()));
        out.add(in.readBytes(in.readableBytes()));
    }
}
