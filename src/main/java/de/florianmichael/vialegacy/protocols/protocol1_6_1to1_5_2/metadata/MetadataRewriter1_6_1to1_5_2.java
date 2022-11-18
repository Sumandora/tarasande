/**
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.2--
 *
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license. The creator assumes no responsibility for any infringements
 * that have arisen, are arising or will arise from this project / file. If this licence is used anywhere,
 * the latest version published by the author Florian Michael (aka EnZaXD) always applies automatically.
 *
 * Changelog:
 *     v1.0:
 *         Added License
 *     v1.1:
 *         Ownership withdrawn
 *     v1.2:
 *         Version-independent validity and automatic renewal
 */

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
