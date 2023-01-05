package de.florianmichael.viabeta.pre_netty.handler;

import com.google.common.collect.EvictingQueue;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.viabeta.pre_netty.viaversion.PreNettyPacketType;
import de.florianmichael.viabeta.pre_netty.viaversion.PreNettySplitter;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import de.florianmichael.viabeta.ViaBeta;

import java.util.List;

public class PreNettyPacketDecoder extends ByteToMessageDecoder {

    protected final UserConnection user;
    private final EvictingQueue<Integer> lastPackets = EvictingQueue.create(8);

    public PreNettyPacketDecoder(final UserConnection user) {
        this.user = user;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        if (!in.isReadable() || in.readableBytes() <= 0) {
            return;
        }
        final PreNettySplitter splitter = this.user.get(PreNettySplitter.class);
        if (splitter == null) {
            ViaBeta.getPlatform().getLogger().severe("Received data, but no splitter is set");
            return;
        }

        while (in.readableBytes() > 0) {
            in.markReaderIndex();
            final int packetId = in.readUnsignedByte();
            final PreNettyPacketType packetType = splitter.getPacketType(packetId);
            if (packetType == null) {
                ViaBeta.getPlatform().getLogger().severe("Encountered undefined packet: " + packetId + " in " + splitter.getProtocolName());
                ViaBeta.getPlatform().getLogger().severe(ByteBufUtil.hexDump(in.readSlice(in.readableBytes())));
                ViaBeta.getPlatform().getLogger().severe("Last 8 read packet ids: " + this.lastPackets);
                ctx.channel().close();
                return;
            }
            this.lastPackets.add(packetId);
            try {
                final int begin = in.readerIndex();
                packetType.getPacketReader().accept(this.user, in);
                final int length = in.readerIndex() - begin;
                in.readerIndex(begin);

                int totalLength = length;
                for (int i = 1; i < 5; ++i) {
                    if ((packetId & -1 << i * 7) == 0) {
                        totalLength += i;
                        break;
                    }
                }

                final ByteBuf buf = ctx.alloc().buffer();
                Type.VAR_INT.writePrimitive(buf, totalLength); // Length
                Type.VAR_INT.writePrimitive(buf, packetId); // id
                buf.writeBytes(in.readSlice(length)); // content
                out.add(buf);
            } catch (IndexOutOfBoundsException e) { // Not enough data
                in.resetReaderIndex();
                return;
            }
        }
    }

}
