package de.florianmichael.viabeta.protocol.alpha.protocola1_2_0_1_2_1_1toa1_1_0_1_1_2_1;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.viabeta.pre_netty.viaversion.PreNettySplitter;
import de.florianmichael.viabeta.protocol.alpha.protocola1_2_2toa1_2_0_1_2_1_1.ClientboundPacketsa1_2_0;
import de.florianmichael.viabeta.protocol.alpha.protocola1_2_2toa1_2_0_1_2_1_1.ServerboundPacketsa1_2_0;
import de.florianmichael.viabeta.protocol.beta.protocolb1_8_0_1tob1_7_0_3.type.Typeb1_7_0_3;

public class Protocola1_2_0_1_2_1_1toa1_1_0_1_1_2_1 extends AbstractProtocol<ClientboundPacketsa1_1_0, ClientboundPacketsa1_2_0, ServerboundPacketsa1_1_0, ServerboundPacketsa1_2_0> {

    public Protocola1_2_0_1_2_1_1toa1_1_0_1_1_2_1() {
        super(ClientboundPacketsa1_1_0.class, ClientboundPacketsa1_2_0.class, ServerboundPacketsa1_1_0.class, ServerboundPacketsa1_2_0.class);
    }

    @Override
    protected void registerPackets() {
        this.registerClientbound(ClientboundPacketsa1_1_0.JOIN_GAME, new PacketHandlers() {
            @Override
            public void register() {
                map(Type.INT); // entity id
                map(Typeb1_7_0_3.STRING); // username
                map(Typeb1_7_0_3.STRING); // password
                create(Type.LONG, 0L); // seed
                create(Type.BYTE, (byte) 0); // dimension id
            }
        });

        this.registerServerbound(State.LOGIN, ServerboundPacketsa1_2_0.LOGIN.getId(), ServerboundPacketsa1_1_0.LOGIN.getId(), new PacketHandlers() {
            @Override
            public void register() {
                map(Type.INT); // protocol id
                map(Typeb1_7_0_3.STRING); // username
                map(Typeb1_7_0_3.STRING); // password
                read(Type.LONG); // seed
                read(Type.BYTE); // dimension id
            }
        });
    }

    @Override
    public void init(UserConnection userConnection) {
        super.init(userConnection);

        userConnection.put(new PreNettySplitter(userConnection, Protocola1_2_0_1_2_1_1toa1_1_0_1_1_2_1.class, ClientboundPacketsa1_1_0::getPacket));
    }
}
