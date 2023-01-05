package de.florianmichael.viabeta.pre_netty.viaversion;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.Protocol;

import java.util.function.IntFunction;

public class PreNettySplitter extends StoredObject {

    private final IntFunction<PreNettyPacketType> packetTypeSupplier;
    private final Class<? extends Protocol<?, ?, ?, ?>> protocolClass;

    public PreNettySplitter(UserConnection user, Class<? extends Protocol<?, ?, ?, ?>> protocolClass, IntFunction<PreNettyPacketType> packetTypeSupplier) {
        super(user);
        this.protocolClass = protocolClass;
        this.packetTypeSupplier = packetTypeSupplier;
    }

    public PreNettyPacketType getPacketType(final int packetId) {
        return this.packetTypeSupplier.apply(packetId);
    }

    public String getProtocolName() {
        return this.protocolClass.getSimpleName();
    }

}
