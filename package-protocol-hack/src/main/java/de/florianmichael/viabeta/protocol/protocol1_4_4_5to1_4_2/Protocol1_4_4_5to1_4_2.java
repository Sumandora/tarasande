package de.florianmichael.viabeta.protocol.protocol1_4_4_5to1_4_2;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.viabeta.protocol.protocol1_4_6_7to1_4_4_5.ClientboundPackets1_4_4;
import de.florianmichael.viabeta.protocol.protocol1_4_4_5to1_4_2.type.Type1_4_2;
import de.florianmichael.viabeta.protocol.protocol1_6_1to1_5_2.ServerboundPackets1_5_2;
import de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.type.impl.MetaType1_6_4;
import de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.type.Type1_6_4;
import de.florianmichael.viabeta.pre_netty.viaversion.PreNettySplitter;

import java.util.List;

public class Protocol1_4_4_5to1_4_2 extends AbstractProtocol<ClientboundPackets1_4_2, ClientboundPackets1_4_4, ServerboundPackets1_5_2, ServerboundPackets1_5_2> {

    public Protocol1_4_4_5to1_4_2() {
        super(ClientboundPackets1_4_2.class, ClientboundPackets1_4_4.class, ServerboundPackets1_5_2.class, ServerboundPackets1_5_2.class);
    }

    @Override
    protected void registerPackets() {
        this.registerClientbound(ClientboundPackets1_4_2.MAP_DATA, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.SHORT); // item id
                map(Type.SHORT); // map id
                map(Type1_4_2.UNSIGNED_BYTE_BYTE_ARRAY, Type.SHORT_BYTE_ARRAY); // data
            }
        });
        this.registerClientbound(ClientboundPackets1_4_2.SPAWN_PLAYER, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // entity id
                map(Type1_6_4.STRING); // name
                map(Type.INT); // x
                map(Type.INT); // y
                map(Type.INT); // z
                map(Type.BYTE); // yaw
                map(Type.BYTE); // pitch
                map(Type.UNSIGNED_SHORT); // item
                map(Type1_4_2.METADATA_LIST, Type1_6_4.METADATA_LIST); // metadata
                handler(wrapper -> rewriteMetadata(wrapper.get(Type1_6_4.METADATA_LIST, 0)));
            }
        });
        this.registerClientbound(ClientboundPackets1_4_2.SPAWN_MOB, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // entity id
                map(Type.UNSIGNED_BYTE); // type id
                map(Type.INT); // x
                map(Type.INT); // y
                map(Type.INT); // z
                map(Type.BYTE); // yaw
                map(Type.BYTE); // pitch
                map(Type.BYTE); // head yaw
                map(Type.SHORT); // velocity x
                map(Type.SHORT); // velocity y
                map(Type.SHORT); // velocity z
                map(Type1_4_2.METADATA_LIST, Type1_6_4.METADATA_LIST); // metadata
                handler(wrapper -> rewriteMetadata(wrapper.get(Type1_6_4.METADATA_LIST, 0)));
            }
        });
        this.registerClientbound(ClientboundPackets1_4_2.ENTITY_METADATA, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // entity id
                map(Type1_4_2.METADATA_LIST, Type1_6_4.METADATA_LIST); // metadata
                handler(wrapper -> rewriteMetadata(wrapper.get(Type1_6_4.METADATA_LIST, 0)));
            }
        });
    }

    private void rewriteMetadata(final List<Metadata> metadataList) {
        for (Metadata metadata : metadataList) {
            metadata.setMetaType(MetaType1_6_4.byId(metadata.metaType().typeId()));
        }
    }

    @Override
    public void init(UserConnection userConnection) {
        userConnection.put(new PreNettySplitter(userConnection, Protocol1_4_4_5to1_4_2.class, ClientboundPackets1_4_2::getPacket));
    }

}
