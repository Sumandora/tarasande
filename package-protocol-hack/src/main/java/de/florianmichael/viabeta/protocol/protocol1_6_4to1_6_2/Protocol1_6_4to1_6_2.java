package de.florianmichael.viabeta.protocol.protocol1_6_4to1_6_2;

import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.ClientboundPackets1_6_4;
import de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.ServerboundPackets1_6_4;

public class Protocol1_6_4to1_6_2 extends AbstractProtocol<ClientboundPackets1_6_4, ClientboundPackets1_6_4, ServerboundPackets1_6_4, ServerboundPackets1_6_4> {

    public Protocol1_6_4to1_6_2() {
        super(ClientboundPackets1_6_4.class, ClientboundPackets1_6_4.class, ServerboundPackets1_6_4.class, ServerboundPackets1_6_4.class);
    }

}
