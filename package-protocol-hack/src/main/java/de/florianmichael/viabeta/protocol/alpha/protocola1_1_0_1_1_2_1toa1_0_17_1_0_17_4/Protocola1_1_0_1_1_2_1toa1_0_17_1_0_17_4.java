package de.florianmichael.viabeta.protocol.alpha.protocola1_1_0_1_1_2_1toa1_0_17_1_0_17_4;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import de.florianmichael.viabeta.protocol.alpha.protocola1_2_0_1_2_1_1toa1_1_0_1_1_2_1.ClientboundPacketsa1_1_0;
import de.florianmichael.viabeta.protocol.alpha.protocola1_2_0_1_2_1_1toa1_1_0_1_1_2_1.ServerboundPacketsa1_1_0;
import de.florianmichael.viabeta.pre_netty.viaversion.PreNettySplitter;

public class Protocola1_1_0_1_1_2_1toa1_0_17_1_0_17_4 extends AbstractProtocol<ClientboundPacketsa1_0_17, ClientboundPacketsa1_1_0, ServerboundPacketsa1_0_17, ServerboundPacketsa1_1_0> {

    public Protocola1_1_0_1_1_2_1toa1_0_17_1_0_17_4() {
        super(ClientboundPacketsa1_0_17.class, ClientboundPacketsa1_1_0.class, ServerboundPacketsa1_0_17.class, ServerboundPacketsa1_1_0.class);
    }

    @Override
    protected void registerPackets() {
        this.cancelServerbound(ServerboundPacketsa1_1_0.COMPLEX_ENTITY);
        this.cancelServerbound(ServerboundPacketsa1_1_0.PLAYER_INVENTORY);
    }

    @Override
    public void init(UserConnection userConnection) {
        super.init(userConnection);

        userConnection.put(new PreNettySplitter(userConnection, Protocola1_1_0_1_1_2_1toa1_0_17_1_0_17_4.class, ClientboundPacketsa1_0_17::getPacket));
    }
}
