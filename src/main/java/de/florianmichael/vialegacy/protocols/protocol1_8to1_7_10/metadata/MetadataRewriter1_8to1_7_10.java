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

package de.florianmichael.vialegacy.protocols.protocol1_8to1_7_10.metadata;

import com.viaversion.viaversion.api.minecraft.entities.Entity1_10Types;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.minecraft.metadata.types.MetaType1_8;
import de.florianmichael.vialegacy.api.metadata.LegacyMetadataRewriter;
import de.florianmichael.vialegacy.protocols.protocol1_8to1_7_10.Protocol1_8to1_7_10;
import de.florianmichael.vialegacy.protocols.protocol1_8to1_7_10.type.MetaType_1_7_6_10;

import java.util.ArrayList;
import java.util.List;

public class MetadataRewriter1_8to1_7_10 extends LegacyMetadataRewriter<Protocol1_8to1_7_10> {

	public MetadataRewriter1_8to1_7_10(Protocol1_8to1_7_10 protocol) {
		super(protocol);
	}

	@Override
	public void rewrite(Entity1_10Types.EntityType entityType, List<Metadata> metadata) {
		for (Metadata entry : new ArrayList<>(metadata)) {
			MetaIndex1_8to1_7_10 metaIndex = MetaIndex1_8to1_7_10.searchIndex(entityType, entry.id());

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
						if (metaIndex.getOldType() == MetaType_1_7_6_10.Short) {
							assert value instanceof Short;
							entry.setValue(((Short) value).intValue());
						}
						if (metaIndex.getOldType() == MetaType_1_7_6_10.Int) {
							entry.setValue(value);
						}
					}
					case Byte -> {
						if (metaIndex.getOldType() == MetaType_1_7_6_10.Int) {
							entry.setValue(((Integer) value).byteValue());
						}
						if (metaIndex.getOldType() == MetaType_1_7_6_10.Byte) {
							entry.setValue(value);
						}
						if (metaIndex == MetaIndex1_8to1_7_10.HUMAN_SKIN_FLAGS) {
							byte flags = (byte) value;
							boolean cape = flags == 2;
							flags = (byte) (cape ? 127 : 125);
							entry.setValue(flags);
						}
						if (metaIndex == MetaIndex1_8to1_7_10.ENTITY_AGEABLE_AGE && metaIndex.getOldType() == MetaType_1_7_6_10.Int) {
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
