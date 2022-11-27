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

package de.florianmichael.vialegacy.api.material;

import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;

public class RMaterial {

	private final int id;
	private final int data;
	private final String name;
	private String resetName;
	private String bracketName;

	public RMaterial(int id) {
		this(id, -1);
	}

	public RMaterial(int id, int data) {
		this(id, data, null);
	}

	public RMaterial(int id, String name) {
		this(id, -1, name);
	}

	public RMaterial(int id, int data, String name) {
		this.id = id;
		this.data = data;
		this.name = name;
		if (name!=null) {
			this.resetName = "§r" + name;
			this.bracketName = " §r§7(" + name + "§r§7)";
		}
	}

	public int getId() {
		return id;
	}

	public int getData() {
		return data;
	}

	public String getName() {
		return name;
	}

	public Item replace(Item item) {
		item.setIdentifier(id);
		if (data!=-1) item.setData((short)data);
		if (name!=null) {
			CompoundTag compoundTag = item.tag()==null ? new CompoundTag() : item.tag();
			if (!compoundTag.contains("display")) compoundTag.put("display", new CompoundTag());
			CompoundTag display = compoundTag.get("display");
			if (display.contains("Name")) {
				StringTag name = display.get("Name");
				if (!name.getValue().equals(resetName) && !name.getValue().endsWith(bracketName))
					name.setValue(name.getValue() + bracketName);
			} else {
				display.put("Name", new StringTag(resetName));
			}
			item.setTag(compoundTag);
		}
		return item;
	}

	public int replaceData(int data) {
		return this.data == -1 ? data : this.data;
	}
}
