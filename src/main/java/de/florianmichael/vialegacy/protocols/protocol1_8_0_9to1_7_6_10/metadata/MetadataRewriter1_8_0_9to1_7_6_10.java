/*
 * Copyright (c) FlorianMichael as EnZaXD 2022
 * Created on 08.04.22, 20:43
 *
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.0--
 *
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license.
 */
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

package de.florianmichael.vialegacy.protocols.protocol1_8_0_9to1_7_6_10.metadata;

import com.viaversion.viaversion.api.minecraft.entities.Entity1_10Types;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.minecraft.metadata.types.MetaType1_8;
import de.florianmichael.vialegacy.api.metadata.LegacyMetadataRewriter;
import de.florianmichael.vialegacy.protocols.protocol1_8_0_9to1_7_6_10.Protocol1_8_0_9to1_7_6_10;
import de.florianmichael.vialegacy.protocols.protocol1_8_0_9to1_7_6_10.type.Meta1_7_6_10Type;

import java.util.ArrayList;
import java.util.List;

public class MetadataRewriter1_8_0_9to1_7_6_10 extends LegacyMetadataRewriter<Protocol1_8_0_9to1_7_6_10> {

	public MetadataRewriter1_8_0_9to1_7_6_10(Protocol1_8_0_9to1_7_6_10 protocol) {
		super(protocol);
	}

	@Override
	public void rewrite(Entity1_10Types.EntityType entityType, boolean isObject, List<Metadata> metadata) {
		for (Metadata entry : new ArrayList<>(metadata)) {
			MetaIndex1_8_0_9to1_7_6_10 metaIndex = MetaIndex1_8_0_9to1_7_6_10.searchIndex(entityType, entry.id());

			try {
				if (metaIndex == null) throw new Exception("Could not find valid metadata");
				if (metaIndex.getNewType() == MetaType1_8.NonExistent) {
					metadata.remove(entry);
					return;
				}
				Object value = entry.getValue();
				entry.setMetaType(metaIndex.getNewType());
				entry.setId(metaIndex.getNewIndex());
				switch (metaIndex.getNewType()) {
					case Int -> {
						if (metaIndex.getOldType() == Meta1_7_6_10Type.Short) {
							assert value instanceof Short;
							entry.setValue(((Short) value).intValue());
						}
						if (metaIndex.getOldType() == Meta1_7_6_10Type.Int) {
							entry.setValue(value);
						}
					}
					case Byte -> {
						if (metaIndex.getOldType() == Meta1_7_6_10Type.Int) {
							entry.setValue(((Integer) value).byteValue());
						}
						if (metaIndex.getOldType() == Meta1_7_6_10Type.Byte) {
							entry.setValue(value);
						}
						if (metaIndex == MetaIndex1_8_0_9to1_7_6_10.HUMAN_SKIN_FLAGS) {
							byte flags = (byte) value;
							boolean cape = flags == 2;
							flags = (byte) (cape ? 127 : 125);
							entry.setValue(flags);
						}
						if (metaIndex == MetaIndex1_8_0_9to1_7_6_10.ENTITY_AGEABLE_AGE && metaIndex.getOldType() == Meta1_7_6_10Type.Int) {
							entry.setValue((int) value < 0 ? -1 : value);
						}
					}
					case Slot -> {
						entry.setValue(protocol.getItemRewriter().handleItemToClient((Item) value));
					}
					case Float, Short, String, Position, Rotation -> entry.setValue(value);
					default -> metadata.remove(entry);
				}
			} catch (Exception e) {
				metadata.remove(entry);
			}
		}
	}
}
