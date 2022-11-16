package de.florianmichael.vialegacy.protocols.protocol1_6_1to1_5_2.metadata;

import com.viaversion.viaversion.api.minecraft.entities.Entity1_10Types;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import de.florianmichael.vialegacy.api.metadata.LegacyMetadataRewriter;
import de.florianmichael.vialegacy.protocols.protocol1_6_1to1_5_2.Protocol1_6_1to1_5_2;
import de.florianmichael.vialegacy.protocols.protocol1_6_1to1_5_2.type.MetaType_1_5_2;

import java.util.ArrayList;
import java.util.List;

public class MetadataRewriter1_6_1to1_5_2 extends LegacyMetadataRewriter<Protocol1_6_1to1_5_2> {

    public MetadataRewriter1_6_1to1_5_2(Protocol1_6_1to1_5_2 protocol) {
        super(protocol);
    }

    @Override
    public void rewrite(Entity1_10Types.EntityType entityType, boolean isObject, List<Metadata> metadata) {
        try {
            for (Metadata entry : new ArrayList<>(metadata)) {
                Object value = entry.getValue();

                if (!isObject) {
                    switch (entry.id()) {
                        case 8, 9, 10 -> entry.setId(entry.id() - 1); // potion color, potion ambient and arrow count in entity
                        case 6 -> entry.setId(11);
                    }

                    if (entry.id() == 5 && entityType != Entity1_10Types.EntityType.ENTITY_HUMAN) {
                        entry.setId(10);
                    }

                    if (entry.id() == 16 && entityType == Entity1_10Types.EntityType.WITHER || entityType == Entity1_10Types.EntityType.ENDER_DRAGON) {
                        entry.setId(6);
                    }

                    if (entry.id() == 18 && entityType == Entity1_10Types.EntityType.WOLF) {
                        entry.setMetaType(MetaType_1_5_2.Float);
                        entry.setValue(((Integer)value).floatValue());
                    }

                    if (entry.id() == 17 && entityType == Entity1_10Types.EntityType.ENTITY_HUMAN) {
                        if (entry.value() instanceof Byte) {
                            entry.setMetaType(MetaType_1_5_2.Float);
                            entry.setValue(((Byte) value).floatValue());
                        } else if (entry.value() instanceof Integer) {
                            entry.setMetaType(MetaType_1_5_2.Float);
                            entry.setValue(((Integer) value).floatValue());
                        }
                    }
                } else {
                    if (entry.id() == 19 && entityType == Entity1_10Types.ObjectType.MINECART.getType() || entityType == Entity1_10Types.ObjectType.BOAT.getType()) {
                        entry.setMetaType(MetaType_1_5_2.Float);
                        entry.setValue(((Integer) value).floatValue());
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }
}
