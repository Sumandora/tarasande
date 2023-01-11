package de.florianmichael.viacursed.protocol.protocol1_14to3D_Shareware.packets;

import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.minecraft.metadata.types.MetaType1_14;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.version.Types1_14;
import de.florianmichael.viacursed.protocol.protocol1_14to3D_Shareware.ClientboundPackets3D_Shareware;
import de.florianmichael.viacursed.protocol.protocol1_14to3D_Shareware.Protocol1_14to3D_Shareware;

import java.util.List;

public class EntityPackets3D_Shareware {

    private final Protocol1_14to3D_Shareware protocol;

    public EntityPackets3D_Shareware(final Protocol1_14to3D_Shareware protocol) {
        this.protocol = protocol;
    }

    public void registerPackets() {
        this.protocol.registerClientbound(ClientboundPackets3D_Shareware.SPAWN_MOB, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT); // 0 - Entity ID
                map(Type.UUID); // 1 - Entity UUID
                map(Type.VAR_INT); // 2 - Entity Type
                map(Type.DOUBLE); // 3 - X
                map(Type.DOUBLE); // 4 - Y
                map(Type.DOUBLE); // 5 - Z
                map(Type.BYTE); // 6 - Yaw
                map(Type.BYTE); // 7 - Pitch
                map(Type.BYTE); // 8 - Head Pitch
                map(Type.SHORT); // 9 - Velocity X
                map(Type.SHORT); // 10 - Velocity Y
                map(Type.SHORT); // 11 - Velocity Z
                map(Types1_14.METADATA_LIST); // 12 - Metadata
                handler(packetWrapper -> handleMetadata(packetWrapper.get(Types1_14.METADATA_LIST, 0)));
            }
        });
        this.protocol.registerClientbound(ClientboundPackets3D_Shareware.SPAWN_PLAYER, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT); // 0 - Entity ID
                map(Type.UUID); // 1 - Player UUID
                map(Type.DOUBLE); // 2 - X
                map(Type.DOUBLE); // 3 - Y
                map(Type.DOUBLE); // 4 - Z
                map(Type.BYTE); // 5 - Yaw
                map(Type.BYTE); // 6 - Pitch
                map(Types1_14.METADATA_LIST); // 7 - Metadata
                handler(packetWrapper -> handleMetadata(packetWrapper.get(Types1_14.METADATA_LIST, 0)));
            }
        });
        this.protocol.registerClientbound(ClientboundPackets3D_Shareware.ENTITY_METADATA, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT); // 0 - Entity ID
                map(Types1_14.METADATA_LIST);
                handler(packetWrapper -> handleMetadata(packetWrapper.get(Types1_14.METADATA_LIST, 0)));
            }
        });
    }

    public void handleMetadata(final List<Metadata> metadataList) {
        for (Metadata metadata : metadataList) {
            if (metadata.metaType() == MetaType1_14.Slot) {
                metadata.setValue(this.protocol.getItemRewriter().handleItemToClient(metadata.value()));
            }
        }
    }

}
