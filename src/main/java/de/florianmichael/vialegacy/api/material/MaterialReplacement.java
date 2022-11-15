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