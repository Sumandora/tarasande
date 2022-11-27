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
import com.viaversion.viaversion.api.minecraft.metadata.types.MetaType1_8;
import com.viaversion.viaversion.util.Pair;
import de.florianmichael.vialegacy.protocols.protocol1_8_0_9to1_7_6_10.type.Meta1_7_6_10Type;

import java.util.HashMap;
import java.util.Optional;

public enum MetaIndex1_8_0_9to1_7_6_10 {

	ENTITY_FLAGS(Entity1_10Types.EntityType.ENTITY, 0, Meta1_7_6_10Type.Byte, MetaType1_8.Byte),
	ENTITY_AIR(Entity1_10Types.EntityType.ENTITY, 1, Meta1_7_6_10Type.Short, MetaType1_8.Short),
	ENTITY_NAME_TAG(Entity1_10Types.EntityType.ENTITY, -1, Meta1_7_6_10Type.NonExistent, 2, MetaType1_8.String),
	ENTITY_NAME_TAG_VISIBILITY(Entity1_10Types.EntityType.ENTITY, -1, Meta1_7_6_10Type.NonExistent, 3, MetaType1_8.Byte),
	ENTITY_SILENT(Entity1_10Types.EntityType.ENTITY, -1, Meta1_7_6_10Type.NonExistent, 4, MetaType1_8.Byte),
	ENTITY_LIVING_HEALTH(Entity1_10Types.EntityType.ENTITY_LIVING, 6, Meta1_7_6_10Type.Float, MetaType1_8.Float),
	ENTITY_LIVING_POTION_EFFECT_COLOR(Entity1_10Types.EntityType.ENTITY_LIVING, 7, Meta1_7_6_10Type.Int, MetaType1_8.Int),
	ENTITY_LIVING_IS_POTION_EFFECT_AMBIENT(Entity1_10Types.EntityType.ENTITY_LIVING, 8, Meta1_7_6_10Type.Byte, MetaType1_8.Byte),
	ENTITY_LIVING_ARROWS(Entity1_10Types.EntityType.ENTITY_LIVING, 9, Meta1_7_6_10Type.Byte, MetaType1_8.Byte),
	ENTITY_LIVING_NAME_TAG(Entity1_10Types.EntityType.ENTITY_LIVING, 10, Meta1_7_6_10Type.String, 2, MetaType1_8.String),
	ENTITY_LIVING_NAME_TAG_VISIBILITY(Entity1_10Types.EntityType.ENTITY_LIVING, 11, Meta1_7_6_10Type.Byte, 3, MetaType1_8.Byte),
	ENTITY_LIVING_AI(Entity1_10Types.EntityType.ENTITY_LIVING, -1, Meta1_7_6_10Type.NonExistent, 15, MetaType1_8.Byte),
	ENTITY_AGEABLE_AGE(Entity1_10Types.EntityType.ENTITY_AGEABLE, 12, Meta1_7_6_10Type.Int, MetaType1_8.Byte),
	ARMOR_STAND_FLAGS(Entity1_10Types.EntityType.ARMOR_STAND, -1, Meta1_7_6_10Type.NonExistent, 10, MetaType1_8.Byte),
	ARMOR_STAND_HEAD_POSITION(Entity1_10Types.EntityType.ARMOR_STAND, -1, Meta1_7_6_10Type.NonExistent, 11, MetaType1_8.Rotation),
	ARMOR_STAND_BODY_POSITION(Entity1_10Types.EntityType.ARMOR_STAND, -1, Meta1_7_6_10Type.NonExistent, 12, MetaType1_8.Rotation),
	ARMOR_STAND_LEFT_ARM_POSITION(Entity1_10Types.EntityType.ARMOR_STAND, -1, Meta1_7_6_10Type.NonExistent, 13, MetaType1_8.Rotation),
	ARMOR_STAND_RIGHT_ARM_POSITION(Entity1_10Types.EntityType.ARMOR_STAND, -1, Meta1_7_6_10Type.NonExistent, 14, MetaType1_8.Rotation),
	ARMOR_STAND_LEFT_LEG_POSITION(Entity1_10Types.EntityType.ARMOR_STAND, -1, Meta1_7_6_10Type.NonExistent, 15, MetaType1_8.Rotation),
	ARMOR_STAND_RIGHT_LEG_POSITION(Entity1_10Types.EntityType.ARMOR_STAND, -1, Meta1_7_6_10Type.NonExistent, 16, MetaType1_8.Rotation),
	HUMAN_SKIN_FLAGS(Entity1_10Types.EntityType.ENTITY_HUMAN, 16, Meta1_7_6_10Type.Byte, 10, MetaType1_8.Byte),
	HUMAN_UNUSED(Entity1_10Types.EntityType.ENTITY_HUMAN, -1, Meta1_7_6_10Type.NonExistent, 16, MetaType1_8.Byte),
	HUMAN_ABSORPTION_HEATS(Entity1_10Types.EntityType.ENTITY_HUMAN, 17, Meta1_7_6_10Type.Float, MetaType1_8.Float),
	HUMAN_SCORE(Entity1_10Types.EntityType.ENTITY_HUMAN, 18, Meta1_7_6_10Type.Int, MetaType1_8.Int),
	HORSE_FLAGS(Entity1_10Types.EntityType.HORSE, 16, Meta1_7_6_10Type.Int, MetaType1_8.Int),
	HORSE_TYPE(Entity1_10Types.EntityType.HORSE, 19, Meta1_7_6_10Type.Byte, MetaType1_8.Byte),
	HORSE_COLOR(Entity1_10Types.EntityType.HORSE, 20, Meta1_7_6_10Type.Int, MetaType1_8.Int),
	HORSE_OWNER(Entity1_10Types.EntityType.HORSE, 21, Meta1_7_6_10Type.String, MetaType1_8.String),
	HORSE_ARMOR(Entity1_10Types.EntityType.HORSE, 22, Meta1_7_6_10Type.Int, MetaType1_8.Int),
	BAT_HANGING(Entity1_10Types.EntityType.BAT, 16, Meta1_7_6_10Type.Byte, MetaType1_8.Byte),
	TAMEABLE_FLAGS(Entity1_10Types.EntityType.ENTITY_TAMEABLE_ANIMAL, 16, Meta1_7_6_10Type.Byte, MetaType1_8.Byte),
	TAMEABLE_OWNER(Entity1_10Types.EntityType.ENTITY_TAMEABLE_ANIMAL, 17, Meta1_7_6_10Type.String, MetaType1_8.String),
	OCELOT_TYPE(Entity1_10Types.EntityType.OCELOT, 18, Meta1_7_6_10Type.Byte, MetaType1_8.Byte),
	WOLF_FLAGS(Entity1_10Types.EntityType.WOLF, 16, Meta1_7_6_10Type.Byte, MetaType1_8.Byte),
	WOLF_HEALTH(Entity1_10Types.EntityType.WOLF, 18, Meta1_7_6_10Type.Float, MetaType1_8.Float),
	WOLF_BEGGING(Entity1_10Types.EntityType.WOLF, 19, Meta1_7_6_10Type.Byte, MetaType1_8.Byte),
	WOLF_COLLAR_COLOR(Entity1_10Types.EntityType.WOLF, 20, Meta1_7_6_10Type.Byte, MetaType1_8.Byte),
	PIG_SADDLE(Entity1_10Types.EntityType.PIG, 16, Meta1_7_6_10Type.Byte, MetaType1_8.Byte),
	SHEEP_COLOR_OR_SHEARED(Entity1_10Types.EntityType.SHEEP, 16, Meta1_7_6_10Type.Byte, MetaType1_8.Byte),
	VILLAGER_TYPE(Entity1_10Types.EntityType.VILLAGER, 16, Meta1_7_6_10Type.Int, MetaType1_8.Int),
	ENDERMAN_CARRIED_BLOCK(Entity1_10Types.EntityType.ENDERMAN, 16, Meta1_7_6_10Type.NonExistent, MetaType1_8.Short),
	ENDERMAN_CARRIED_BLOCK_DATA(Entity1_10Types.EntityType.ENDERMAN, 17, Meta1_7_6_10Type.NonExistent, MetaType1_8.Byte),
	ENDERMAN_IS_SCREAMING(Entity1_10Types.EntityType.ENDERMAN, 18, Meta1_7_6_10Type.Byte, MetaType1_8.Byte),
	ZOMBIE_CHILD(Entity1_10Types.EntityType.ZOMBIE, 12, Meta1_7_6_10Type.Byte, MetaType1_8.Byte),
	ZOMBIE_VILLAGER(Entity1_10Types.EntityType.ZOMBIE, 13, Meta1_7_6_10Type.Byte, MetaType1_8.Byte),
	ZOMBIE_CONVERTING(Entity1_10Types.EntityType.ZOMBIE, 14, Meta1_7_6_10Type.Byte, MetaType1_8.Byte),
	BLAZE_ON_FIRE(Entity1_10Types.EntityType.BLAZE, 16, Meta1_7_6_10Type.Byte, MetaType1_8.Byte),
	SPIDER_CLIMBING(Entity1_10Types.EntityType.SPIDER, 16, Meta1_7_6_10Type.Byte, MetaType1_8.Byte),
	CREEPER_STATE(Entity1_10Types.EntityType.CREEPER, 16, Meta1_7_6_10Type.Byte, MetaType1_8.Byte),
	CREEPER_POWERED(Entity1_10Types.EntityType.CREEPER, 17, Meta1_7_6_10Type.Byte, MetaType1_8.Byte),
	GHAST_STATE(Entity1_10Types.EntityType.GHAST, 16, Meta1_7_6_10Type.Byte, MetaType1_8.Byte),
	GHAST_IS_POWERED(Entity1_10Types.EntityType.GHAST, 17, Meta1_7_6_10Type.NonExistent, MetaType1_8.Byte),
	SLIME_SIZE(Entity1_10Types.EntityType.SLIME, 16, Meta1_7_6_10Type.Byte, MetaType1_8.Byte),
	SKELETON_TYPE(Entity1_10Types.EntityType.SKELETON, 13, Meta1_7_6_10Type.Byte, MetaType1_8.Byte),
	WITCH_AGRESSIVE(Entity1_10Types.EntityType.WITCH, 21, Meta1_7_6_10Type.Byte, MetaType1_8.Byte),
	IRON_GOLEM_IS_PLAYER_CREATED(Entity1_10Types.EntityType.IRON_GOLEM, 16, Meta1_7_6_10Type.Byte, MetaType1_8.Byte),
	WITHER_WATCHED_TAGRET_1(Entity1_10Types.EntityType.WITHER, 17, Meta1_7_6_10Type.Int, MetaType1_8.Int),
	WITHER_WATCHED_TAGRET_2(Entity1_10Types.EntityType.WITHER, 18, Meta1_7_6_10Type.Int, MetaType1_8.Int),
	WITHER_WATCHED_TAGRET_3(Entity1_10Types.EntityType.WITHER, 19, Meta1_7_6_10Type.Int, MetaType1_8.Int),
	WITHER_INVULNERABLE_TIME(Entity1_10Types.EntityType.WITHER, 20, Meta1_7_6_10Type.Int, MetaType1_8.Int),
	GUARDIAN_FLAGS(Entity1_10Types.EntityType.GUARDIAN, 16, Meta1_7_6_10Type.NonExistent, MetaType1_8.Byte),
	GUARDIAN_TARGET(Entity1_10Types.EntityType.GUARDIAN, 17, Meta1_7_6_10Type.NonExistent, MetaType1_8.Int),
	BOAT_TIME_SINCE_HIT(Entity1_10Types.EntityType.BOAT, 17, Meta1_7_6_10Type.Int, MetaType1_8.Int),
	BOAT_FORWARD_DIRECTION(Entity1_10Types.EntityType.BOAT, 18, Meta1_7_6_10Type.Int, MetaType1_8.Int),
	BOAT_DAMAGE_TAKEN(Entity1_10Types.EntityType.BOAT, 19, Meta1_7_6_10Type.Float, MetaType1_8.Float),
	MINECART_SHAKING_POWER(Entity1_10Types.EntityType.MINECART_ABSTRACT, 17, Meta1_7_6_10Type.Int, MetaType1_8.Int),
	MINECART_SHAKING_DIRECTION(Entity1_10Types.EntityType.MINECART_ABSTRACT, 18, Meta1_7_6_10Type.Int, MetaType1_8.Int),
	MINECART_DAMAGE_TAKEN(Entity1_10Types.EntityType.MINECART_ABSTRACT, 19, Meta1_7_6_10Type.Float, MetaType1_8.Float),
	MINECART_BLOCK_INSIDE(Entity1_10Types.EntityType.MINECART_ABSTRACT, 20, Meta1_7_6_10Type.Int, MetaType1_8.Int),
	MINECART_BLOCK_Y(Entity1_10Types.EntityType.MINECART_ABSTRACT, 21, Meta1_7_6_10Type.Int, MetaType1_8.Int),
	MINECART_SHOW_BLOCK(Entity1_10Types.EntityType.MINECART_ABSTRACT, 22, Meta1_7_6_10Type.Byte, MetaType1_8.Byte),
	FURNACE_MINECART_IS_POWERED(Entity1_10Types.EntityType.MINECART_FURNACE, 16, Meta1_7_6_10Type.Byte, MetaType1_8.Byte),
	ITEM_ITEM(Entity1_10Types.EntityType.DROPPED_ITEM, 10, Meta1_7_6_10Type.Slot, MetaType1_8.Slot),
	ARROW_IS_CRITICAL(Entity1_10Types.EntityType.ARROW, 16, Meta1_7_6_10Type.Byte, MetaType1_8.Byte),
	FIREWORK_INFO(Entity1_10Types.EntityType.FIREWORK, 8, Meta1_7_6_10Type.Slot, MetaType1_8.Slot),
	ITEM_FRAME_ITEM(Entity1_10Types.EntityType.ITEM_FRAME, 2, Meta1_7_6_10Type.Slot, 8, MetaType1_8.Slot),
	ITEM_FRAME_ROTATION(Entity1_10Types.EntityType.ITEM_FRAME, 3, Meta1_7_6_10Type.Byte, 9, MetaType1_8.Byte),
	ENDER_CRYSTAL_HEALTH(Entity1_10Types.EntityType.ENDER_CRYSTAL, 8, Meta1_7_6_10Type.Int, 9, MetaType1_8.Int),
	;

	private static final HashMap<Pair<Entity1_10Types.EntityType, Integer>, MetaIndex1_8_0_9to1_7_6_10> metadataRewrites = new HashMap<>();

	static {
		for (MetaIndex1_8_0_9to1_7_6_10 index : MetaIndex1_8_0_9to1_7_6_10.values())
			metadataRewrites.put(new Pair<>(index.getClazz(), index.getIndex()), index);
	}

	private Entity1_10Types.EntityType clazz;
	private int newIndex;
	private MetaType1_8 newType;
	private Meta1_7_6_10Type oldType;
	private int index;

	MetaIndex1_8_0_9to1_7_6_10(Entity1_10Types.EntityType type, int index, Meta1_7_6_10Type oldType, MetaType1_8 newType) {
		this.clazz = type;
		this.index = index;
		this.newIndex = index;
		this.oldType = oldType;
		this.newType = newType;
	}

	MetaIndex1_8_0_9to1_7_6_10(Entity1_10Types.EntityType type, int index, Meta1_7_6_10Type oldType, int newIndex, MetaType1_8 newType) {
		this.clazz = type;
		this.index = index;
		this.oldType = oldType;
		this.newIndex = newIndex;
		this.newType = newType;
	}

	private static Optional<MetaIndex1_8_0_9to1_7_6_10> getIndex(Entity1_10Types.EntityType type, int index) {
		Pair pair = new Pair<>(type, index);
		if (metadataRewrites.containsKey(pair)) {
			return Optional.of(metadataRewrites.get(pair));
		}

		return Optional.empty();
	}

	public Entity1_10Types.EntityType getClazz() {
		return clazz;
	}

	public int getNewIndex() {
		return newIndex;
	}

	public MetaType1_8 getNewType() {
		return newType;
	}

	public Meta1_7_6_10Type getOldType() {
		return oldType;
	}

	public int getIndex() {
		return index;
	}

	public static MetaIndex1_8_0_9to1_7_6_10 searchIndex(Entity1_10Types.EntityType type, int index) {
		Entity1_10Types.EntityType currentType = type;
		do {
			Optional<MetaIndex1_8_0_9to1_7_6_10> optMeta = getIndex(currentType, index);

			if (optMeta.isPresent()) {
				return optMeta.get();
			}

			currentType = currentType.getParent();
		} while (currentType != null);

		return null;
	}
}
