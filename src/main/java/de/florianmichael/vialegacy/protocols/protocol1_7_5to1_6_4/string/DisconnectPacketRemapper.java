package de.florianmichael.vialegacy.protocols.protocol1_7_5to1_6_4.string;

import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.vialegacy.api.type.TypeRegistry_1_6_4;

public class DisconnectPacketRemapper extends PacketRemapper {

    @Override
    public void registerMap() {
        map(TypeRegistry_1_6_4.STRING, Type.STRING);
//        handler((pw) -> pw.set(Type.STRING, 0, ComponentSerializer.toString(TextComponent.fromLegacyText(pw.get(Type.STRING, 0)))));
    }
}
