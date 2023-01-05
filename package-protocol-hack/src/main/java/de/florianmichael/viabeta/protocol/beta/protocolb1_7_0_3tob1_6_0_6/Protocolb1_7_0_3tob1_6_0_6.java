package de.florianmichael.viabeta.protocol.beta.protocolb1_7_0_3tob1_6_0_6;

import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import de.florianmichael.viabeta.protocol.beta.protocolb1_8_0_1tob1_7_0_3.ClientboundPacketsb1_7;
import de.florianmichael.viabeta.protocol.beta.protocolb1_8_0_1tob1_7_0_3.ServerboundPacketsb1_7;

public class Protocolb1_7_0_3tob1_6_0_6 extends AbstractProtocol<ClientboundPacketsb1_7, ClientboundPacketsb1_7, ServerboundPacketsb1_7, ServerboundPacketsb1_7> {

    public Protocolb1_7_0_3tob1_6_0_6() {
        super(ClientboundPacketsb1_7.class, ClientboundPacketsb1_7.class, ServerboundPacketsb1_7.class, ServerboundPacketsb1_7.class);
    }
}
