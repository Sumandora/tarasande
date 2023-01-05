package de.florianmichael.viabeta.protocol.alpha.protocola1_2_3_1_2_3_4toa1_2_2;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.viabeta.pre_netty.viaversion.PreNettySplitter;
import de.florianmichael.viabeta.protocol.alpha.protocola1_2_3_5_1_2_6toa1_2_3_1_2_3_4.ClientboundPacketsa1_2_3;
import de.florianmichael.viabeta.protocol.alpha.protocolb1_0_1_1_1toa1_2_3_5_1_2_6.ServerboundPacketsa1_2_6;

public class Protocola1_2_3_1_2_3_4toa1_2_2 extends AbstractProtocol<ClientboundPacketsa1_2_2, ClientboundPacketsa1_2_3, ServerboundPacketsa1_2_2, ServerboundPacketsa1_2_6> {

    public Protocola1_2_3_1_2_3_4toa1_2_2() {
        super(ClientboundPacketsa1_2_2.class, ClientboundPacketsa1_2_3.class, ServerboundPacketsa1_2_2.class, ServerboundPacketsa1_2_6.class);
    }

    @Override
    protected void registerPackets() {
        this.registerClientbound(ClientboundPacketsa1_2_2.JOIN_GAME, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    final PacketWrapper updateHealth = PacketWrapper.create(ClientboundPacketsa1_2_3.UPDATE_HEALTH, wrapper.user());
                    updateHealth.write(Type.BYTE, (byte) 20); // health

                    wrapper.send(Protocola1_2_3_1_2_3_4toa1_2_2.class);
                    updateHealth.send(Protocola1_2_3_1_2_3_4toa1_2_2.class);
                    wrapper.cancel();
                });
            }
        });

        this.registerServerbound(ServerboundPacketsa1_2_6.INTERACT_ENTITY, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // player id
                map(Type.INT); // entity id
                read(Type.BYTE); // mode
            }
        });
        this.cancelServerbound(ServerboundPacketsa1_2_6.RESPAWN);
    }

    @Override
    public void init(UserConnection userConnection) {
        super.init(userConnection);

        userConnection.put(new PreNettySplitter(userConnection, Protocola1_2_3_1_2_3_4toa1_2_2.class, ClientboundPacketsa1_2_2::getPacket));
    }
}
