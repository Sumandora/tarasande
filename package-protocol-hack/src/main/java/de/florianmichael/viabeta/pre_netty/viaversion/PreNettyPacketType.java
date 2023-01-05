package de.florianmichael.viabeta.pre_netty.viaversion;

import com.viaversion.viaversion.api.connection.UserConnection;
import io.netty.buffer.ByteBuf;

import java.util.function.BiConsumer;

public interface PreNettyPacketType {

    BiConsumer<UserConnection, ByteBuf> getPacketReader();

}
