package de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.metadata;

import com.viaversion.viaversion.api.minecraft.entities.Entity1_10Types;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import de.florianmichael.viabeta.ViaBeta;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.Protocol1_8to1_7_6_10;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class MetadataRewriter {

    public static void transform(Entity1_10Types.EntityType type, List<Metadata> list) {
        for (Metadata entry : new ArrayList<>(list)) {
            final MetaIndex1_8to1_7_6 metaIndex = MetaIndex1_8to1_7_6.searchIndex(type, entry.id());
            try {
                if (metaIndex == null) {
                    ViaBeta.getPlatform().getLogger().warning("Could not find valid metaindex entry for " + type.name() + ": " + entry);
                    list.remove(entry);
                    continue;
                }

                final Object value = entry.getValue();
                entry.setTypeAndValue(metaIndex.getOldType(), value); // check if metadata type is the expected type from metaindex entry
                entry.setMetaTypeUnsafe(metaIndex.getNewType());
                entry.setId(metaIndex.getNewIndex());

                if (metaIndex == MetaIndex1_8to1_7_6.ENTITY_AGEABLE_AGE) {
                    entry.setValue(((int) value) < 0 ? (byte) -1 : (byte) 0);
                    continue;
                } else if (metaIndex == MetaIndex1_8to1_7_6.ITEM_FRAME_ROTATION) {
                    entry.setValue(Integer.valueOf((Byte) value * 2).byteValue());
                    continue;
                } else if (metaIndex == MetaIndex1_8to1_7_6.ENDERMAN_CARRIED_BLOCK) {
                    final byte id = (byte) value;
                    Metadata blockDataMeta = null;
                    for (Metadata metadata : list) {
                        if (metadata.id() == MetaIndex1_8to1_7_6.ENDERMAN_CARRIED_BLOCK_DATA.getOldIndex()) {
                            blockDataMeta = metadata;
                            list.remove(blockDataMeta);
                            break;
                        }
                    }
                    final byte data = blockDataMeta != null ? (Byte) blockDataMeta.getValue() : 0;
                    entry.setValue((short) (id | (data << 12)));
                    continue;
                } else if (metaIndex == MetaIndex1_8to1_7_6.HUMAN_SKIN_FLAGS) {
                    byte flags = (byte) value;
                    boolean cape = flags == 2;
                    flags = (byte) (cape ? 127 : 125);
                    entry.setValue(flags);
                    continue;
                }

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
                        Protocol1_8to1_7_6_10.ITEM_REWRITER.rewriteRead((Item) value);
                        break;
                    case String:
                    case Position:
                    case Rotation:
                        break;
                    default:
                        ViaBeta.getPlatform().getLogger().warning("1.7.10 MetaDataType: Unhandled Type: " + metaIndex.getNewType() + " " + entry);
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
