package de.florianmichael.vialegacy.protocol.splitter;

import io.netty.buffer.ByteBuf;

public interface IPacketSplitter {

    void read(final ByteBuf buffer, final TransformInstanceUtil transformer);
}
