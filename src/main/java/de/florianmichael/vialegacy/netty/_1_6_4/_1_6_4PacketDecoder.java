/*
 * Copyright (c) FlorianMichael as EnZaXD 2022
 * Created on 6/24/22, 5:38 PM
 *
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.0--
 *
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license.
 */

package de.florianmichael.vialegacy.netty._1_6_4;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.vialegacy.ViaLegacy;
import de.florianmichael.vialegacy.protocol.LegacyProtocolVersion;
import de.florianmichael.vialegacy.protocol.SplitterTracker;
import de.florianmichael.vialegacy.protocol.splitter.IPacketSplitter;
import de.florianmichael.vialegacy.protocol.splitter.TransformInstanceUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;
import java.util.Map;

public class _1_6_4PacketDecoder extends ByteToMessageDecoder {

    private final UserConnection connection;

    public _1_6_4PacketDecoder(final UserConnection connection) {
        this.connection = connection;
    }

    private int readPacket(final IPacketSplitter splitterLogic, final ByteBuf buf) {
        buf.markReaderIndex();
        int start = buf.readerIndex();

        if (splitterLogic != null)
            splitterLogic.read(buf, new TransformInstanceUtil());

        int now = buf.readerIndex() - start;
        buf.resetReaderIndex();

        return now;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        @SuppressWarnings("ConstantConditions") final Map<Integer, IPacketSplitter> splitterAdapter = connection.getProtocolInfo().getUser().get(SplitterTracker.class).splitter;
        if (in.readableBytes() != 0) {
            ByteBuf draft = null;
            int backupIdx = -1;
            try {
                backupIdx = in.readerIndex();

                final int packetId = in.readUnsignedByte();
                final int packetLength = readPacket(splitterAdapter.get(packetId), in);

                final byte[] packet = new byte[packetLength];
                in.readBytes(packet);

                draft = ctx.alloc().buffer();
                Type.VAR_INT.writePrimitive(draft, packetId);
                draft.writeBytes(packet);

            } catch (Throwable t) {
                in.readerIndex(backupIdx);
            }

            if (draft == null)
                return;
            out.add(draft);
        }
    }
}
