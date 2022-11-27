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
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectOpenHashMap;

public class MaterialReplacement {

	private final Int2ObjectMap<RMaterial> itemReplacements = new Int2ObjectOpenHashMap<>();
	private final Int2ObjectMap<RMaterial> blockReplacements = new Int2ObjectOpenHashMap<>();

	public void registerItem(int id, RMaterial RMaterial) {
		registerItem(id, -1, RMaterial);
	}

	public void registerBlock(int id, RMaterial RMaterial) {
		registerBlock(id, -1, RMaterial);
	}

	public void registerItemBlock(int id, RMaterial RMaterial) {
		registerItemBlock(id, -1, RMaterial);
	}

	public void registerItem(int id, int data, RMaterial RMaterial) {
		itemReplacements.put(combine(id, data), RMaterial);
	}

	public void registerBlock(int id, int data, RMaterial RMaterial) {
		blockReplacements.put(combine(id, data), RMaterial);
	}

	public void registerItemBlock(int id, int data, RMaterial RMaterial) {
		registerItem(id, data, RMaterial);
		registerBlock(id, data, RMaterial);
	}

	public Item replace(Item item) {
		if (item == null) {
			return null;
		}
		RMaterial RMaterial = itemReplacements.get(combine(item.identifier(), item.data()));
		if (RMaterial ==null) RMaterial = itemReplacements.get(combine(item.identifier(), -1));
		return RMaterial ==null ? item : RMaterial.replace(item);
	}

	public RMaterial replace(int id, int data) {
		RMaterial RMaterial = blockReplacements.get(combine(id, data));
		if (RMaterial == null) {
			RMaterial = blockReplacements.get(combine(id, -1));
		}
		return RMaterial;
	}

	public static int combine(int id, int data) {
		return (id << 16) | (data & 0xFFFF);
	}
}