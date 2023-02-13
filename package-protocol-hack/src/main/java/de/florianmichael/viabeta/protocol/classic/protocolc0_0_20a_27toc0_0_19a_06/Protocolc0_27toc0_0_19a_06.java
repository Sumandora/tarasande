package de.florianmichael.viabeta.protocol.classic.protocolc0_0_20a_27toc0_0_19a_06;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.viabeta.pre_netty.viaversion.PreNettySplitter;
import de.florianmichael.viabeta.protocol.classic.protocola1_0_15toc0_28_30.ServerboundPacketsc0_28;
import de.florianmichael.viabeta.protocol.classic.protocola1_0_15toc0_28_30.data.ClassicBlocks;
import de.florianmichael.viabeta.protocol.classic.protocola1_0_15toc0_28_30.storage.ClassicBlockRemapper;
import de.florianmichael.viabeta.protocol.classic.protocola1_0_15toc0_28_30.type.Typec0_30;
import de.florianmichael.viabeta.protocol.classic.protocolc0_0_19a_06toc0_0_18a_02.Protocolc0_0_19a_06toc0_0_18a_02;
import de.florianmichael.viabeta.protocol.classic.protocolc0_28_30toc0_0_20a_27.ClientboundPacketsc0_20a;

public class Protocolc0_27toc0_0_19a_06 extends AbstractProtocol<ClientboundPacketsc0_19a, ClientboundPacketsc0_20a, ServerboundPacketsc0_19a, ServerboundPacketsc0_28> {

    public Protocolc0_27toc0_0_19a_06() {
        super(ClientboundPacketsc0_19a.class, ClientboundPacketsc0_20a.class, ServerboundPacketsc0_19a.class, ServerboundPacketsc0_28.class);
    }

    @Override
    protected void registerPackets() {
        this.registerClientbound(ClientboundPacketsc0_19a.JOIN_GAME, new PacketHandlers() {
            @Override
            public void register() {
                map(Type.BYTE); // protocol id
                map(Typec0_30.STRING); // title
                map(Typec0_30.STRING); // motd
                create(Type.BYTE, (byte) 0); // op level
            }
        });

        this.registerServerbound(State.LOGIN, ServerboundPacketsc0_28.LOGIN.getId(), ServerboundPacketsc0_19a.LOGIN.getId(), new PacketHandlers() {
            @Override
            public void register() {
                map(Type.BYTE); // protocol id
                map(Typec0_30.STRING); // username
                map(Typec0_30.STRING); // mp pass
                read(Type.BYTE); // op level
            }
        });
    }

    @Override
    public void init(UserConnection userConnection) {
        super.init(userConnection);

        userConnection.put(new PreNettySplitter(userConnection, Protocolc0_27toc0_0_19a_06.class, ClientboundPacketsc0_19a::getPacket));

        final ClassicBlockRemapper previousRemapper = userConnection.get(ClassicBlockRemapper.class);
        userConnection.put(new ClassicBlockRemapper(userConnection, previousRemapper.getMapper(), o -> {
            int block = previousRemapper.getReverseMapper().getInt(o);

            if (!userConnection.getProtocolInfo().getPipeline().contains(Protocolc0_0_19a_06toc0_0_18a_02.class)) {
                if (block != ClassicBlocks.STONE && block != ClassicBlocks.DIRT && block != ClassicBlocks.WOOD && block != ClassicBlocks.SAPLING && block != ClassicBlocks.GRAVEL && block != ClassicBlocks.LOG && block != ClassicBlocks.LEAVES && block != ClassicBlocks.SPONGE && block != ClassicBlocks.GLASS) {
                    block = ClassicBlocks.STONE;
                }
            }

            return block;
        }));
    }
}
