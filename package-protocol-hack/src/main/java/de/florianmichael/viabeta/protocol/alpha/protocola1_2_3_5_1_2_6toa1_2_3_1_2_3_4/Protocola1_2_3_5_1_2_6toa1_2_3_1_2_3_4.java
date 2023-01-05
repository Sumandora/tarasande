package de.florianmichael.viabeta.protocol.alpha.protocola1_2_3_5_1_2_6toa1_2_3_1_2_3_4;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.viabeta.protocol.alpha.protocolb1_0_1_1_1toa1_2_3_5_1_2_6.ClientboundPacketsa1_2_6;
import de.florianmichael.viabeta.protocol.alpha.protocolb1_0_1_1_1toa1_2_3_5_1_2_6.ServerboundPacketsa1_2_6;
import de.florianmichael.viabeta.pre_netty.viaversion.PreNettySplitter;

public class Protocola1_2_3_5_1_2_6toa1_2_3_1_2_3_4 extends AbstractProtocol<ClientboundPacketsa1_2_3, ClientboundPacketsa1_2_6, ServerboundPacketsa1_2_6, ServerboundPacketsa1_2_6> {

    public Protocola1_2_3_5_1_2_6toa1_2_3_1_2_3_4() {
        super(ClientboundPacketsa1_2_3.class, ClientboundPacketsa1_2_6.class, ServerboundPacketsa1_2_6.class, ServerboundPacketsa1_2_6.class);
    }

    @Override
    protected void registerPackets() {
        this.registerClientbound(ClientboundPacketsa1_2_3.ENTITY_VELOCITY, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // entity id
                map(Type.SHORT, Type.SHORT, v -> (short) (v / 4.0F)); // velocity x
                map(Type.SHORT, Type.SHORT, v -> (short) (v / 4.0F)); // velocity y
                map(Type.SHORT, Type.SHORT, v -> (short) (v / 4.0F)); // velocity z
            }
        });
    }

    @Override
    public void init(UserConnection userConnection) {
        super.init(userConnection);

        userConnection.put(new PreNettySplitter(userConnection, Protocola1_2_3_5_1_2_6toa1_2_3_1_2_3_4.class, ClientboundPacketsa1_2_3::getPacket));
    }
}
