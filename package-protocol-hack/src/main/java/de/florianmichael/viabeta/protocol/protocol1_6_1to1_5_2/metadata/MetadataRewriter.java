package de.florianmichael.viabeta.protocol.protocol1_6_1to1_5_2.metadata;

import com.viaversion.viaversion.api.minecraft.entities.Entity1_10Types;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import de.florianmichael.viabeta.ViaBeta;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class MetadataRewriter {

    public static void transform(Entity1_10Types.EntityType type, List<Metadata> list) {
        for (Metadata entry : new ArrayList<>(list)) {
            final MetaIndex1_6_1to1_5_2 metaIndex = MetaIndex1_6_1to1_5_2.searchIndex(type, entry.id());
            try {
                if (metaIndex == null) continue;

                final Object value = entry.getValue();
                entry.setTypeAndValue(metaIndex.getOldType(), value); // check if metadata type is the expected type from metaindex entry
                entry.setMetaTypeUnsafe(metaIndex.getNewType());
                entry.setId(metaIndex.getNewIndex());

                switch (metaIndex.getNewType()) {
                    case Byte:
                        entry.setValue(((Number) value).byteValue());
                        break;
                    case Short:
                        entry.setValue(((Number) value).shortValue());
                        break;
                    case Int:
                        entry.setValue(((Number) value).intValue());
                        break;
                    case Float:
                        entry.setValue(((Number) value).floatValue());
                        break;
                    case Slot:
                    case String:
                    case Position:
                        break;
                    default:
                        ViaBeta.getPlatform().getLogger().warning("1.5.2 MetaDataType: Unhandled Type: " + metaIndex.getNewType() + " " + entry);
                        list.remove(entry);
                        break;
                }
            } catch (Throwable e) {
                ViaBeta.getPlatform().getLogger().log(Level.WARNING, "Error rewriting metadata entry for " + type.name() + ": " + entry, e);
                list.remove(entry);
            }
        }
    }

}
