package de.florianmichael.viaprotocolhack.netty;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.exception.CancelCodecException;
import com.viaversion.viaversion.exception.CancelDecoderException;
import com.viaversion.viaversion.exception.InformativeException;
import com.viaversion.viaversion.util.PipelineUtil;
import de.florianmichael.viaprotocolhack.ViaProtocolHack;
import de.florianmichael.viaprotocolhack.event.PipelineReorderEvent;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

@ChannelHandler.Sharable
public class CustomViaDecodeHandler extends MessageToMessageDecoder<ByteBuf> {
    private final UserConnection info;

    public CustomViaDecodeHandler(UserConnection info) {
        this.info = info;
    }

    public UserConnection getInfo() {
        return info;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf bytebuf, List<Object> out) throws Exception {
        if (!info.checkIncomingPacket()) throw CancelDecoderException.generate(null);
        if (!info.shouldTransformPacket()) {
            out.add(bytebuf.retain());
            return;
        }

        ByteBuf transformedBuf = ctx.alloc().buffer().writeBytes(bytebuf);
        try {
            info.transformIncoming(transformedBuf, CancelDecoderException::generate);

            out.add(transformedBuf.retain());
        } finally {
            transformedBuf.release();
        }
    }

    private void reorder(ChannelHandlerContext ctx) {
        final String[] order = ViaProtocolHack.instance().provider().nettyOrder();

        final int decoderIndex = ctx.pipeline().names().indexOf(order[0]);
        if (decoderIndex == -1) return;

        if (decoderIndex > ctx.pipeline().names().indexOf(NettyConstants.HANDLER_DECODER_NAME)) {
            ChannelHandler encoder = ctx.pipeline().get(NettyConstants.HANDLER_ENCODER_NAME);
            ChannelHandler decoder = ctx.pipeline().get(NettyConstants.HANDLER_DECODER_NAME);

            ctx.pipeline().remove(encoder);
            ctx.pipeline().remove(decoder);

            ctx.pipeline().addAfter(order[1], NettyConstants.HANDLER_ENCODER_NAME, encoder);
            ctx.pipeline().addAfter(order[0], NettyConstants.HANDLER_DECODER_NAME, decoder);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (PipelineUtil.containsCause(cause, CancelCodecException.class)) return;

        if ((PipelineUtil.containsCause(cause, InformativeException.class)
                && info.getProtocolInfo().getState() != State.HANDSHAKE)
                || Via.getManager().debugHandler().enabled()) {
            cause.printStackTrace();
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof PipelineReorderEvent) {
            reorder(ctx);
        }
        super.userEventTriggered(ctx, evt);
    }
}