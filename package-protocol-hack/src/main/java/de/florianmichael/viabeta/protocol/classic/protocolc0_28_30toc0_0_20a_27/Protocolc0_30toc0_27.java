package de.florianmichael.viabeta.protocol.classic.protocolc0_28_30toc0_0_20a_27;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import de.florianmichael.viabeta.pre_netty.viaversion.PreNettySplitter;
import de.florianmichael.viabeta.protocol.classic.protocola1_0_15toc0_28_30.ClientboundPacketsc0_28;
import de.florianmichael.viabeta.protocol.classic.protocola1_0_15toc0_28_30.ServerboundPacketsc0_28;
import de.florianmichael.viabeta.protocol.classic.protocola1_0_15toc0_28_30.data.ClassicBlocks;
import de.florianmichael.viabeta.protocol.classic.protocola1_0_15toc0_28_30.storage.ClassicBlockRemapper;
import de.florianmichael.viabeta.protocol.classic.protocolc0_0_20a_27toc0_0_19a_06.Protocolc0_27toc0_0_19a_06;

public class Protocolc0_30toc0_27 extends AbstractProtocol<ClientboundPacketsc0_20a, ClientboundPacketsc0_28, ServerboundPacketsc0_28, ServerboundPacketsc0_28> {

    public Protocolc0_30toc0_27() {
        super(ClientboundPacketsc0_20a.class, ClientboundPacketsc0_28.class, ServerboundPacketsc0_28.class, ServerboundPacketsc0_28.class);
    }

    @Override
    public void init(UserConnection userConnection) {
        super.init(userConnection);

        userConnection.put(new PreNettySplitter(userConnection, Protocolc0_30toc0_27.class, ClientboundPacketsc0_20a::getPacket));

        final ClassicBlockRemapper previousRemapper = userConnection.get(ClassicBlockRemapper.class);
        userConnection.put(new ClassicBlockRemapper(userConnection, previousRemapper.getMapper(), o -> {
            int block = previousRemapper.getReverseMapper().getInt(o);

            if (!userConnection.getProtocolInfo().getPipeline().contains(Protocolc0_27toc0_0_19a_06.class)) {
                if (block == ClassicBlocks.GOLD_ORE || block == ClassicBlocks.IRON_ORE || block == ClassicBlocks.COAL_ORE || block == ClassicBlocks.SLAB || block == ClassicBlocks.BRICK || block == ClassicBlocks.TNT || block == ClassicBlocks.BOOKSHELF || block == ClassicBlocks.MOSSY_COBBLESTONE || block == ClassicBlocks.OBSIDIAN) {
                    block = ClassicBlocks.STONE;
                }
            }

            return block;
        }));
    }
}
