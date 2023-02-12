package de.florianmichael.viabeta.protocol.alpha.protocola1_2_2toa1_2_0_1_2_1_1;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.viabeta.pre_netty.viaversion.PreNettySplitter;
import de.florianmichael.viabeta.protocol.alpha.protocola1_2_3_1_2_3_4toa1_2_2.ClientboundPacketsa1_2_2;
import de.florianmichael.viabeta.protocol.alpha.protocola1_2_3_1_2_3_4toa1_2_2.ServerboundPacketsa1_2_2;

public class Protocola1_2_2toa1_2_0_1_2_1_1 extends AbstractProtocol<ClientboundPacketsa1_2_0, ClientboundPacketsa1_2_2, ServerboundPacketsa1_2_0, ServerboundPacketsa1_2_2> {

    public Protocola1_2_2toa1_2_0_1_2_1_1() {
        super(ClientboundPacketsa1_2_0.class, ClientboundPacketsa1_2_2.class, ServerboundPacketsa1_2_0.class, ServerboundPacketsa1_2_2.class);
    }

    @Override
    protected void registerPackets() {
        this.registerClientbound(ClientboundPacketsa1_2_0.SPAWN_MOB, new PacketHandlers() {
            @Override
            public void register() {
                map(Type.INT); // entity id
                map(Type.UNSIGNED_BYTE); // type id
                map(Type.INT); // x
                map(Type.INT); // y
                map(Type.INT); // z
                map(Type.BYTE); // yaw
                map(Type.BYTE); // pitch
                handler(wrapper -> {
                    if (wrapper.get(Type.UNSIGNED_BYTE, 0) == 91) {
                        wrapper.set(Type.UNSIGNED_BYTE, 0, (short) 93);
                    }
                });
            }
        });

        this.cancelServerbound(ServerboundPacketsa1_2_2.INTERACT_ENTITY);
    }

    @Override
    public void init(UserConnection userConnection) {
        super.init(userConnection);

        userConnection.put(new PreNettySplitter(userConnection, Protocola1_2_2toa1_2_0_1_2_1_1.class, ClientboundPacketsa1_2_0::getPacket));
    }
}
