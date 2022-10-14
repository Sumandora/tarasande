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
import de.florianmichael.vialegacy.api.type.meta.MetaType_1_7_6_10;
import de.florianmichael.vialegacy.protocols.protocol1_8to1_7_10.item.ItemRewriter;

import java.util.ArrayList;
import java.util.List;

public class MetadataRewriter {

	public static void transform(Entity1_10Types.EntityType type, List<Metadata> list) {
		for (Metadata entry : new ArrayList<>(list)) {
			MetaIndex1_8to1_7_6_10 metaIndex = MetaIndex1_8to1_7_6_10.searchIndex(type, entry.id());

			try {
				if (metaIndex == null) throw new Exception("Could not find valid metadata");
				if (metaIndex.getNewType() == MetaType1_8.NonExistent) {
					list.remove(entry);
					return;
				}
				Object value = entry.getValue();
				entry.setMetaType(metaIndex.getNewType());
				entry.setId(metaIndex.getNewIndex());
				switch (metaIndex.getNewType()) {
					case Int -> {
						if (metaIndex.getOldType() == MetaType_1_7_6_10.Byte) {
							entry.setValue(((Byte) value).intValue());
							if (metaIndex == MetaIndex1_8to1_7_6_10.ENTITY_AGEABLE_AGE) {
								if ((Integer) entry.getValue() < 0) {
									entry.setValue(-25000);
								}
							}
						}
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
						if (metaIndex == MetaIndex1_8to1_7_6_10.HUMAN_SKIN_FLAGS) {
							byte flags = (byte) value;
							boolean cape = flags == 2;
							flags = (byte) (cape ? 127 : 125);
							entry.setValue(flags);
						}
					}
					case Slot -> {
						entry.setValue(ItemRewriter.toClient((Item) value));
					}
					case Float, Short, String, Position, Rotation -> entry.setValue(value);
					default -> list.remove(entry);
				}
			} catch (Exception e) {
				list.remove(entry);
			}
		}
	}
}
