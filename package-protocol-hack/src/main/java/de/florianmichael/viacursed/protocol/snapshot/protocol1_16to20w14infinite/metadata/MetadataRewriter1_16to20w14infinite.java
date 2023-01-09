package de.florianmichael.viacursed.protocol.snapshot.protocol1_16to20w14infinite.metadata;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_16Types;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.minecraft.metadata.types.MetaType1_14;
import com.viaversion.viaversion.api.type.types.Particle;
import com.viaversion.viaversion.rewriter.EntityRewriter;
import de.florianmichael.viacursed.protocol.snapshot.protocol1_16to20w14infinite.Protocol1_16to20w14infinite;

import java.util.List;

public class MetadataRewriter1_16to20w14infinite extends EntityRewriter<Protocol1_16to20w14infinite> {

    public MetadataRewriter1_16to20w14infinite(Protocol1_16to20w14infinite protocol) {
        super(protocol);
        mapEntityType(Entity20w14infiniteTypes.ZOMBIE_PIGMAN, Entity1_16Types.ZOMBIFIED_PIGLIN);
        mapTypes(Entity20w14infiniteTypes.values(), Entity1_16Types.class);
    }

    @Override
    public EntityType typeFromId(int type) {
        return Entity1_16Types.getTypeFromId(type);
    }

    @Override
    public void handleMetadata(int entityId, EntityType type, Metadata metadata, List<Metadata> metadatas, UserConnection connection) throws Exception {
        if (metadata.metaType() == MetaType1_14.Slot) {
            metadata.setValue(this.protocol.getItemRewriter().handleItemToClient(metadata.value()));
        } else if (metadata.metaType() == MetaType1_14.BlockID) {
            int data = (int) metadata.getValue();
            metadata.setValue(protocol.getMappingData().getNewBlockStateId(data));
        } else if (metadata.metaType() == MetaType1_14.PARTICLE) {
            rewriteParticle((Particle) metadata.getValue());
        }

        if (type == null) return;

        if (type.isOrHasParent(Entity1_16Types.MINECART_ABSTRACT)
                && metadata.id() == 10) {
            // Convert to new block id
            int data = (int) metadata.getValue();
            metadata.setValue(protocol.getMappingData().getNewBlockStateId(data));
        }

        if (type.isOrHasParent(Entity1_16Types.ABSTRACT_ARROW)) {
            if (metadata.id() == 8) {
                metadatas.remove(metadata);
            } else if (metadata.id() > 8) {
                metadata.setId(metadata.id() - 1);
            }
        }
    }

}
