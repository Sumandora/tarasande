package de.florianmichael.viasnapshot.protocol.protocol3D_Sharewareto1_14;

import com.viaversion.viabackwards.api.BackwardsProtocol;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.ClientboundPackets1_14;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.ServerboundPackets1_14;
import de.florianmichael.viasnapshot.protocol.protocol1_14to3D_Shareware.ClientboundPackets3D_Shareware;
import de.florianmichael.viasnapshot.protocol.protocol1_14to3D_Shareware.ServerboundPackets3D_Shareware;

public class Protocol3D_Sharewareto1_14 extends BackwardsProtocol<ClientboundPackets1_14, ClientboundPackets3D_Shareware, ServerboundPackets1_14, ServerboundPackets3D_Shareware> {

    public Protocol3D_Sharewareto1_14() {
        super(ClientboundPackets1_14.class, ClientboundPackets3D_Shareware.class, ServerboundPackets1_14.class, ServerboundPackets3D_Shareware.class);
    }

    @Override
    protected void registerPackets() {
        this.cancelClientbound(ClientboundPackets1_14.UPDATE_VIEW_POSITION);
        this.cancelClientbound(ClientboundPackets1_14.ACKNOWLEDGE_PLAYER_DIGGING);
    }
}
