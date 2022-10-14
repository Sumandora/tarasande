package de.florianmichael.vialegacy.protocols.protocol1_7_10to1_7_5;

import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.ValueTransformer;
import com.viaversion.viaversion.api.type.Type;

public class DashUuidAdder extends ValueTransformer<String, String> {

    public DashUuidAdder() {
        super(Type.STRING);
    }

    @Override
    public String transform(PacketWrapper packetWrapper, String s) {
        return s.substring(0, 8) + "-" +
                s.substring(8, 12) + "-" +
                s.substring(12, 16) + "-" +
                s.substring(16, 20) + "-" +
                s.substring(20);
    }
}
