package de.florianmichael.viabeta.protocol.beta.protocolb1_1_2tob1_0_1_1;

import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import de.florianmichael.viabeta.protocol.beta.protocolb1_2_0_2tob1_1_2.ClientboundPacketsb1_1;
import de.florianmichael.viabeta.protocol.beta.protocolb1_2_0_2tob1_1_2.ServerboundPacketsb1_1;

public class Protocolb1_1_2tob1_0_1_1 extends AbstractProtocol<ClientboundPacketsb1_1, ClientboundPacketsb1_1, ServerboundPacketsb1_1, ServerboundPacketsb1_1> {

    public Protocolb1_1_2tob1_0_1_1() {
        super(ClientboundPacketsb1_1.class, ClientboundPacketsb1_1.class, ServerboundPacketsb1_1.class, ServerboundPacketsb1_1.class);
    }
}
