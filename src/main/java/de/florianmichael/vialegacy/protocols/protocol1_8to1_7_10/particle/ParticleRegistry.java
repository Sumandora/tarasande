package de.florianmichael.vialegacy.protocols.protocol1_8to1_7_10.particle;

import java.util.HashMap;

public enum ParticleRegistry {

	EXPLOSION_NORMAL("explode"),
	EXPLOSION_LARGE("largeexplode"),
	EXPLOSION_HUGE("hugeexplosion"),
	FIREWORKS_SPARK("fireworksSpark"),
	WATER_BUBBLE("bubble"),
	WATER_SPLASH("splash"),
	WATER_WAKE("wake"),
	SUSPENDED("suspended"),
	SUSPENDED_DEPTH("depthsuspend"),
	CRIT("crit"),
	CRIT_MAGIC("magicCrit"),
	SMOKE_NORMAL("smoke"),
	SMOKE_LARGE("largesmoke"),
	SPELL("spell"),
	SPELL_INSTANT("instantSpell"),
	SPELL_MOB("mobSpell"),
	SPELL_MOB_AMBIENT("mobSpellAmbient"),
	SPELL_WITCH("witchMagic"),
	DRIP_WATER("dripWater"),
	DRIP_LAVA("dripLava"),
	VILLAGER_ANGRY("angryVillager"),
	VILLAGER_HAPPY("happyVillager"),
	TOWN_AURA("townaura"),
	NOTE("note"),
	PORTAL("portal"),
	ENCHANTMENT_TABLE("enchantmenttable"),
	FLAME("flame"),
	LAVA("lava"),
	FOOTSTEP("footstep"),
	CLOUD("cloud"),
	REDSTONE("reddust"),
	SNOWBALL("snowballpoof"),
	SNOW_SHOVEL("snowshovel"),
	SLIME("slime"),
	HEART("heart"),
	BARRIER("barrier"),
	ICON_CRACK("iconcrack", 2),
	BLOCK_CRACK("blockcrack", 1),
	BLOCK_DUST("blockdust", 1),
	WATER_DROP("droplet"),
	ITEM_TAKE("take"),
	MOB_APPEARANCE("mobappearance");

	public final String name;
	public final int extra;
	private static final HashMap<String, ParticleRegistry> particleMap = new HashMap();

	ParticleRegistry(String name) {
		this(name, 0);
	}

	ParticleRegistry(String name, int extra) {
		this.name = name;
		this.extra = extra;
	}

	public static ParticleRegistry find(String part) {
		return particleMap.get(part);
	}

	public static ParticleRegistry find(int id) {
		if (id<0) return null;
		ParticleRegistry[] values = ParticleRegistry.values();
		return id>=values.length ? null : values[id];
	}

	static {
		ParticleRegistry[] particles = values();

		for (ParticleRegistry particle : particles)
			particleMap.put(particle.name, particle);
	}
}
