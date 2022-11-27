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

package de.florianmichael.vialegacy.protocols.protocol1_8_0_9to1_7_6_10.item;

import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.*;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ChatRewriter;
import de.florianmichael.vialegacy.api.item.LegacyItemRewriter;
import de.florianmichael.vialegacy.protocols.protocol1_8_0_9to1_7_6_10.Protocol1_8_0_9to1_7_6_10;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemRewriter1_8_0_9to1_7_6_10 extends LegacyItemRewriter<Protocol1_8_0_9to1_7_6_10> {

	public static final Map<Short, String> ENCHANTMENTS = new HashMap<>();

	static {
		ENCHANTMENTS.put((short) 1, "I");
		ENCHANTMENTS.put((short) 2, "II");
		ENCHANTMENTS.put((short) 3, "III");
		ENCHANTMENTS.put((short) 4, "IV");
		ENCHANTMENTS.put((short) 5, "V");
		ENCHANTMENTS.put((short) 6, "VI");
		ENCHANTMENTS.put((short) 7, "VII");
		ENCHANTMENTS.put((short) 8, "VIII");
		ENCHANTMENTS.put((short) 9, "IX");
		ENCHANTMENTS.put((short) 10, "X");
	}

	public ItemRewriter1_8_0_9to1_7_6_10(Protocol1_8_0_9to1_7_6_10 protocol) {
		super(protocol);
	}

	@Override
	public @Nullable Item handleItemToClient(@Nullable Item item) {
		if (item == null) return null;

		CompoundTag tag = item.tag();
		if (tag == null) {
			item.setTag(tag = new CompoundTag());
		}

		final CompoundTag viaVersionTag = new CompoundTag();
		tag.put("ViaLegacy1_8_0_9to1_7_6_10", viaVersionTag);

		viaVersionTag.put("id", new ShortTag((short) item.identifier()));
		viaVersionTag.put("data", new ShortTag(item.data()));

		CompoundTag display = tag.get("display");

		if (display != null && display.contains("Name")) {
			viaVersionTag.put("displayName", new StringTag((String) display.get("Name").getValue()));
		}

		if (display != null && display.contains("Lore")) {
			viaVersionTag.put("lore", new ListTag(((ListTag)display.get("Lore")).getValue()));
		}

		if (tag.contains("ench") || tag.contains("StoredEnchantments")) {
			final ListTag enchTag = tag.contains("ench") ? tag.get("ench") : tag.get("StoredEnchantments");
			final List<Tag> lore = new ArrayList<>();

			for (Tag ench : new ArrayList<>(enchTag.getValue())) {
				short id = ((NumberTag) ((CompoundTag)ench).get("id")).asShort();
				short lvl = ((NumberTag) ((CompoundTag)ench).get("lvl")).asShort();
				String s;
				if (id == 8) {
					s  = "§r§7Depth Strider ";
				} else {
					continue;
				}
				enchTag.remove(ench);
				s += ENCHANTMENTS.getOrDefault(lvl, "enchantment.level." + lvl);
				lore.add(new StringTag(s));
			}
			if (!lore.isEmpty()) {
				if (display == null) {
					tag.put("display", display = new CompoundTag());
					viaVersionTag.put("noDisplay", new ByteTag());
				}
				ListTag loreTag = display.get("Lore");
				if (loreTag == null) {
					display.put("Lore", loreTag = new ListTag(StringTag.class));
				}
				lore.addAll(loreTag.getValue());
				loreTag.setValue(lore);
			}
		}

		if (item.identifier() == 387 && tag.contains("pages")) {
			ListTag pages = tag.get("pages");
			ListTag oldPages = new ListTag(StringTag.class);
			viaVersionTag.put("pages", oldPages);

			for (int i = 0; i < pages.size(); i++) {
				StringTag page = pages.get(i);
				String value = page.getValue();
				oldPages.add(new StringTag(value));
				value = ChatRewriter.legacyTextToJson(value).toString();
				page.setValue(value);
			}
		}

		protocol.materialReplacement().replace(item);

		if (viaVersionTag.size() == 2 && (short)viaVersionTag.get("id").getValue() == item.identifier() && (short)viaVersionTag.get("data").getValue() == item.data()) {
			item.tag().remove("ViaLegacy1_8_0_9to1_7_6_10");
			if (item.tag().isEmpty()) item.setTag(null);
		}

		return item;
	}

	@Override
	public @Nullable Item handleItemToServer(@Nullable Item item) {
		if (item == null) return null;

		CompoundTag tag = item.tag();

		if (tag == null || !item.tag().contains("ViaLegacy1_8_0_9to1_7_6_10")) return item;

		CompoundTag viaVersionTag = tag.remove("ViaLegacy1_8_0_9to1_7_6_10");

		item.setIdentifier((short) viaVersionTag.get("id").getValue());
		item.setData((Short) viaVersionTag.get("data").getValue());

		if (viaVersionTag.contains("noDisplay")) tag.remove("display");

		if (viaVersionTag.contains("displayName")) {
			CompoundTag display = tag.get("display");
			if (display==null) tag.put("display", display = new CompoundTag());
			StringTag name = display.get("Name");
			if (name==null) display.put("Name", new StringTag((String) viaVersionTag.get("displayName").getValue()));
			else name.setValue((String) viaVersionTag.get("displayName").getValue());
		} else if (tag.contains("display")) {
			((CompoundTag)tag.get("display")).remove("Name");
		}

		if (item.identifier() == 387) {
			ListTag oldPages = viaVersionTag.get("pages");
			tag.remove("pages");
			tag.put("pages", oldPages);
		}

		return item;
	}
}
