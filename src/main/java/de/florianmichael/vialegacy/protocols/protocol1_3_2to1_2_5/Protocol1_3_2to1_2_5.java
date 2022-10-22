/*
 * Copyright (c) FlorianMichael as EnZaXD 2022
 * Created on 24.06.22, 13:55
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

package de.florianmichael.vialegacy.protocols.protocol1_3_2to1_2_5;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.item.DataItem;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ShortTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import de.florianmichael.vialegacy.ViaLegacy;
import de.florianmichael.vialegacy.api.type.TypeRegistry1_2_5;
import de.florianmichael.vialegacy.api.type.TypeRegistry1_7_6_10;
import de.florianmichael.vialegacy.api.type.TypeRegistry_1_4_2;
import de.florianmichael.vialegacy.api.type.TypeRegistry_1_6_4;
import de.florianmichael.vialegacy.api.via.EnZaProtocol;
import de.florianmichael.vialegacy.protocols.protocol1_3_2to1_2_5.storage.LevelDataStorage;
import de.florianmichael.vialegacy.protocols.protocol1_4_2to1_3_2.ClientboundPackets1_3_2;
import de.florianmichael.vialegacy.protocols.protocol1_4_2to1_3_2.ServerboundPackets1_3_2;
import de.florianmichael.vialegacy.protocols.protocol1_4_7to1_4_5.storage.DimensionStorage;
import de.florianmichael.vialegacy.protocols.protocol1_7_5to1_6_4.Protocol1_7_5to1_6_4;
import de.florianmichael.vialegacy.protocols.protocol1_7_5to1_6_4.ServerboundLoginPackets1_6_4;

import java.util.ArrayList;
import java.util.UUID;

@SuppressWarnings("ConstantConditions")
public class Protocol1_3_2to1_2_5 extends EnZaProtocol<ClientboundPackets1_2_5, ClientboundPackets1_3_2, ServerboundPackets1_2_5, ServerboundPackets1_3_2> {

	private static final BiMap<Integer, String> ENTITY_IDS;

	static {
		HashBiMap<String, Integer> entityMap = HashBiMap.create();
		entityMap.put("Item", 1);
		entityMap.put("XPOrb", 2);
		entityMap.put("Painting", 9);
		entityMap.put("Arrow", 10);
		entityMap.put("Snowball", 11);
		entityMap.put("Fireball", 12);
		entityMap.put("SmallFireball", 13);
		entityMap.put("ThrownEnderpearl", 14);
		entityMap.put("EyeOfEnderSignal", 15);
		entityMap.put("ThrownPotion", 16);
		entityMap.put("ThrownExpBottle", 17);
		entityMap.put("PrimedTnt", 20);
		entityMap.put("FallingSand", 21);
		entityMap.put("Minecart", 40);
		entityMap.put("Boat", 41);
		entityMap.put("Mob", 48);
		entityMap.put("Monster", 49);
		entityMap.put("Creeper", 50);
		entityMap.put("Skeleton", 51);
		entityMap.put("Spider", 52);
		entityMap.put("Giant", 53);
		entityMap.put("Zombie", 54);
		entityMap.put("Slime", 55);
		entityMap.put("Ghast", 56);
		entityMap.put("PigZombie", 57);
		entityMap.put("Enderman", 58);
		entityMap.put("CaveSpider", 59);
		entityMap.put("Silverfish", 60);
		entityMap.put("Blaze", 61);
		entityMap.put("LavaSlime", 62);
		entityMap.put("EnderDragon", 63);
		entityMap.put("Pig", 90);
		entityMap.put("Sheep", 91);
		entityMap.put("Cow", 92);
		entityMap.put("Chicken", 93);
		entityMap.put("Squid", 94);
		entityMap.put("Wolf", 95);
		entityMap.put("MushroomCow", 96);
		entityMap.put("SnowMan", 97);
		entityMap.put("Ozelot", 98);
		entityMap.put("VillagerGolem", 99);
		entityMap.put("Villager", 120);
		entityMap.put("EnderCrystal", 200);

		ENTITY_IDS = entityMap.inverse();
	}

	public Protocol1_3_2to1_2_5() {
		super(ClientboundPackets1_2_5.class, ClientboundPackets1_3_2.class, ServerboundPackets1_2_5.class, ServerboundPackets1_3_2.class);
	}

	@Override
	protected void registerPackets() {
		super.registerPackets();

		this.registerServerbound(State.LOGIN, ServerboundLoginPackets1_6_4.CLIENT_PROTOCOL.getId(), ServerboundLoginPackets1_6_4.CLIENT_PROTOCOL.getId(), new PacketRemapper() {
			@Override
			public void registerMap() {
				handler((pw) -> {
					pw.read(Type.UNSIGNED_BYTE); // Protocol ID
					final String username = pw.read(TypeRegistry_1_6_4.STRING);
					final String host = pw.read(TypeRegistry_1_6_4.STRING);
					final int port = pw.read(Type.INT);

					pw.clearPacket();
					pw.write(TypeRegistry_1_6_4.STRING, username + ";" + host + ":" + port);
				});
			}
		});

		this.cancelServerbound(ServerboundPackets1_3_2.CLIENT_SETTINGS);

		this.registerServerbound(ServerboundPackets1_3_2.CLIENT_STATUS, ServerboundPackets1_2_5.RESPAWN, new PacketRemapper() {
			@Override
			public void registerMap() {
				handler((pw) -> {
					final byte mode = pw.read(Type.BYTE);
					if (mode == 0x00) {
						pw.cancel();
						return;
					}

					pw.clearPacket();

					final LevelDataStorage dataStorage = pw.user().get(LevelDataStorage.class);

					pw.write(Type.INT, pw.user().get(DimensionStorage.class).getDimension());
					pw.write(Type.BYTE, (byte) 1); // Client always send 1
					pw.write(Type.BYTE, (byte) dataStorage.gameMode);
					pw.write(Type.SHORT, (short) dataStorage.worldHeight);
					pw.write(TypeRegistry_1_6_4.STRING, dataStorage.levelType);
				});
			}
		});

		this.registerClientbound(ClientboundPackets1_2_5.JOIN_GAME, new PacketRemapper() {
			@Override
			public void registerMap() {
				handler(pw -> {
					final int entityId = pw.read(Type.INT);
					pw.read(TypeRegistry_1_6_4.STRING); // Not documented
					final String levelType = pw.read(TypeRegistry_1_6_4.STRING);
					final byte gamemode = pw.read(Type.INT).byteValue();
					final byte dimension = pw.read(Type.INT).byteValue();
					final byte difficulty = pw.read(Type.BYTE);
					final byte worldHeight = pw.read(Type.BYTE);
					final byte maxPlayers = pw.read(Type.BYTE);

					final LevelDataStorage dataStorage = pw.user().get(LevelDataStorage.class);
					dataStorage.levelType = levelType;
					dataStorage.gameMode = gamemode;
					dataStorage.worldHeight = worldHeight;

					pw.clearPacket();

					pw.write(Type.INT, entityId);
					pw.write(TypeRegistry_1_6_4.STRING, levelType);
					pw.write(Type.BYTE, gamemode);
					pw.write(Type.BYTE, dimension);
					pw.write(Type.BYTE, difficulty);
					pw.write(Type.BYTE, worldHeight);
					pw.write(Type.BYTE, maxPlayers);
				});
			}
		});

		this.registerClientbound(ClientboundPackets1_2_5.GAME_EVENT, new PacketRemapper() {
			@Override
			public void registerMap() {
				handler(pw -> {
					final int reason = pw.read(Type.BYTE);
					if (reason == 3) {
						pw.user().get(LevelDataStorage.class).gameMode = pw.read(Type.BYTE);
					}
				});
			}
		});

		this.registerServerbound(ServerboundPackets1_3_2.CLICK_WINDOW, new PacketRemapper() {
			@Override
			public void registerMap() {
				map(Type.BYTE); // Window id
				map(Type.SHORT); // Slot
				map(Type.BYTE); // Button
				map(Type.SHORT); // Action
				map(Type.BYTE); // Mode
				map(TypeRegistry1_7_6_10.COMPRESSED_NBT_ITEM, TypeRegistry1_2_5.COMPRESSED_NBT_ITEM); // Item
			}
		});

		this.registerServerbound(ServerboundPackets1_3_2.CREATIVE_INVENTORY_ACTION, new PacketRemapper() {
			@Override
			public void registerMap() {
				map(Type.SHORT); // Slot
				map(TypeRegistry1_7_6_10.COMPRESSED_NBT_ITEM, TypeRegistry1_2_5.COMPRESSED_NBT_ITEM); // Item
			}
		});

		this.registerServerbound(ServerboundPackets1_3_2.PLAYER_BLOCK_PLACEMENT, new PacketRemapper() {
			@Override
			public void registerMap() {
				map(Type.INT); // X-Position
				map(Type.UNSIGNED_BYTE); // Y-Position
				map(Type.INT); // Z-Position
				map(Type.UNSIGNED_BYTE); // Direction
				map(TypeRegistry1_7_6_10.COMPRESSED_NBT_ITEM, TypeRegistry1_2_5.COMPRESSED_NBT_ITEM); // Slot
				map(Type.BYTE, Type.NOTHING); // Cursor-X
				map(Type.BYTE, Type.NOTHING); // Cursor-Y
				map(Type.BYTE, Type.NOTHING); // Cursor-Z
			}
		});

		this.registerServerbound(ServerboundPackets1_3_2.PLAYER_ABILITIES, new PacketRemapper() {
			@Override
			public void registerMap() {
				handler((pw) -> {
					byte flags = pw.read(Type.BYTE);
					pw.read(Type.BYTE); // Fly speed
					pw.read(Type.BYTE); // Walk speed
					pw.clearPacket();

					pw.write(Type.BOOLEAN, (flags & 8) == 8); // Disable damage
					pw.write(Type.BOOLEAN, (flags & 2) == 2); // Is flying
					pw.write(Type.BOOLEAN, (flags & 4) == 4); // Allow flying
					pw.write(Type.BOOLEAN, (flags & 1) == 1); // Is creative mode
				});
			}
		});

		this.cancelServerbound(ServerboundPackets1_3_2.TAB_COMPLETE);

		this.registerClientbound(State.LOGIN, ClientboundLoginPackets1_2_5.HANDSHAKE.getId(), ClientboundLoginPackets1_2_5.HANDSHAKE.getId(), new PacketRemapper() {
			@Override
			public void registerMap() {
				handler((pw) -> {
					final String serverId = pw.read(TypeRegistry_1_6_4.STRING);
					pw.cancel();

					if (!serverId.equals("-")) {
						ViaLegacy.getProvider().sendJoinServer_1_2_5(serverId);
					}

					final PacketWrapper loginSuccess = PacketWrapper.create(ClientboundLoginPackets1_2_5.HANDSHAKE, pw.user());

					loginSuccess.write(Type.STRING, UUID.randomUUID().toString().replace("-", ""));
					loginSuccess.write(Type.STRING, pw.user().getProtocolInfo().getUsername());
					loginSuccess.send(Protocol1_7_5to1_6_4.class);
					pw.user().getProtocolInfo().setState(State.PLAY);

					final PacketWrapper login = PacketWrapper.create(ServerboundPackets1_2_5.JOIN_GAME, pw.user());
					login.write(Type.INT, Math.abs(pw.user().getProtocolInfo().getServerProtocolVersion()));

					login.write(TypeRegistry_1_6_4.STRING, pw.user().getProtocolInfo().getUsername());
					login.write(TypeRegistry_1_6_4.STRING, "");

					login.write(Type.INT, 0);
					login.write(Type.INT, 0);

					login.write(Type.BYTE, (byte) 0);
					login.write(Type.BYTE, (byte) 0);
					login.write(Type.BYTE, (byte) 0);

					login.sendToServer(Protocol1_3_2to1_2_5.class);
				});
			}
		});


		this.registerClientbound(ClientboundPackets1_2_5.ENTITY_EQUIPMENT, new PacketRemapper() {
			@Override
			public void registerMap() {
				map(Type.INT); // Entity-Id
				map(Type.SHORT); // Slot
				handler((pw) -> {
					final short id = pw.read(Type.SHORT);
					final short damage = pw.read(Type.SHORT);

					pw.write(TypeRegistry1_7_6_10.COMPRESSED_NBT_ITEM, id == -1 ? null : new DataItem(id, (byte) 1, damage, null));
				});
			}
		});

		this.registerClientbound(ClientboundPackets1_2_5.DESTROY_ENTITIES, new PacketRemapper() {
			@Override
			public void registerMap() {
				handler((pw) -> {
					final int entityId = pw.read(Type.INT);
					pw.clearPacket();

					pw.write(Type.UNSIGNED_BYTE, (short) 1);
					pw.write(Type.INT, entityId);
				});
			}
		});

		this.registerClientbound(ClientboundPackets1_2_5.ENTITY_METADATA, new PacketRemapper() {
			@Override
			public void registerMap() {
				map(Type.INT); // Entity-Id
				map(TypeRegistry1_2_5.METADATA_LIST, TypeRegistry_1_4_2.METADATA_LIST); // Metadata list
			}
		});

		this.registerClientbound(ClientboundPackets1_2_5.BLOCK_ENTITY_DATA, new PacketRemapper() {
			@Override
			public void registerMap() {
				map(Type.INT); // X-Position
				map(Type.SHORT); // Y-Position
				map(Type.INT); // Z-Position

				map(Type.UNSIGNED_BYTE); // Action-Type

				handler((pw) -> {
					int param1 = pw.read(Type.INT);
					int param2 = pw.read(Type.INT);
					int param3 = pw.read(Type.INT);
					if (pw.get(Type.UNSIGNED_BYTE, 0) == 1) {
						final CompoundTag tag = new CompoundTag();
						tag.put("EntityId", new StringTag(ENTITY_IDS.get(param1)));
						tag.put("Delay", new ShortTag((short) 20));
						tag.put("x", new IntTag(pw.get(Type.INT, 0)));
						tag.put("y", new IntTag(pw.get(Type.SHORT, 0)));
						tag.put("z", new IntTag(pw.get(Type.INT, 1)));
						pw.write(TypeRegistry1_7_6_10.COMPRESSED_NBT, tag);
					}
				});
			}
		});

		this.registerClientbound(ClientboundPackets1_2_5.SET_SLOT, new PacketRemapper() {
			@Override
			public void registerMap() {
				map(Type.BYTE); // Window id
				map(Type.SHORT); // Slot
				map(TypeRegistry1_2_5.COMPRESSED_NBT_ITEM, TypeRegistry1_7_6_10.COMPRESSED_NBT_ITEM); // Item
			}
		});

		this.registerClientbound(ClientboundPackets1_2_5.WINDOW_ITEMS, new PacketRemapper() {
			@Override
			public void registerMap() {
				map(Type.BYTE); // Window id
				handler((pw) -> {
					byte wId = pw.get(Type.BYTE, 0);
					short itms = pw.read(Type.SHORT);
					Item[] items = new Item[itms];
					for (int i = 0; i < itms; i++) {
						items[i] = pw.read(TypeRegistry1_2_5.COMPRESSED_NBT_ITEM);
					}
					pw.clearPacket();

					pw.write(Type.BYTE, wId);
					pw.write(TypeRegistry1_7_6_10.COMPRESSED_NBT_ITEM_ARRAY, items);
				});
			}
		});

		this.registerClientbound(ClientboundPackets1_2_5.CREATIVE_INVENTORY_ACTION, new PacketRemapper() {
			@Override
			public void registerMap() {
				map(Type.SHORT); // Slot
				map(TypeRegistry1_2_5.COMPRESSED_NBT_ITEM, TypeRegistry1_7_6_10.COMPRESSED_NBT_ITEM); // Item
			}
		});

		this.registerClientbound(ClientboundPackets1_2_5.PLAYER_ABILITIES, new PacketRemapper() {
			@Override
			public void registerMap() {
				handler((pw) -> {
					boolean disableDamage = pw.read(Type.BOOLEAN);
					boolean isFlying = pw.read(Type.BOOLEAN);
					boolean allowFlying = pw.read(Type.BOOLEAN);
					boolean isCreativeMode = pw.read(Type.BOOLEAN);
					pw.clearPacket();

					byte flags = 0;
					if (disableDamage) {
						flags |= 8;
					}
					if (isFlying) {
						flags |= 2;
					}
					if (allowFlying) {
						flags |= 4;
					}
					if (isCreativeMode) {
						flags |= 1;
					}

					pw.write(Type.BYTE, flags);
					pw.write(Type.BYTE, (byte) ((int) (0.05F * 255) & 0xFF)); // Fly speed
					pw.write(Type.BYTE, (byte) ((int) (0.1F * 255) & 0xFF)); // Walk speed
				});
			}
		});

		this.registerClientbound(ClientboundPackets1_2_5.SPAWN_PLAYER, new PacketRemapper() {
			@Override
			public void registerMap() {
				map(Type.INT); // Entity-Id

				map(TypeRegistry_1_6_4.STRING); // Player name

				map(Type.INT); // X-Position
				map(Type.INT); // Y-Position
				map(Type.INT); // Z-Position

				map(Type.BYTE); // Yaw
				map(Type.BYTE); // Pitch

				map(Type.SHORT); // Current item

				handler((pw) -> pw.write(TypeRegistry_1_4_2.METADATA_LIST, new ArrayList<>()));
			}
		});

		this.registerClientbound(ClientboundPackets1_2_5.SPAWN_MOB, new PacketRemapper() {
			@Override
			public void registerMap() {
				map(Type.INT); // Entity-Id
				map(Type.BYTE); // Player name
				map(Type.INT); // X-Position
				map(Type.INT); // Y-Position
				map(Type.INT); // Z-Position
				map(Type.BYTE); // Pitch
				map(Type.BYTE); // Head pitch
				map(Type.BYTE); // Yaw
				handler((pw) -> {
					pw.write(Type.SHORT, (short) 0); // Velocity-X
					pw.write(Type.SHORT, (short) 0); // Velocity-Y
					pw.write(Type.SHORT, (short) 0); // Velocity-Z
				});
				map(TypeRegistry1_2_5.METADATA_LIST, TypeRegistry_1_4_2.METADATA_LIST); // Metadata list
			}
		});

		this.registerClientbound(ClientboundPackets1_2_5.PRE_CHUNK, ClientboundPackets1_3_2.CHUNK_DATA, new PacketRemapper() {
			@Override
			public void registerMap() {
				handler(packetWrapper -> {
					final int chunkX = packetWrapper.read(Type.INT);
					final int chunkZ = packetWrapper.read(Type.INT);
					final short mode = packetWrapper.read(Type.UNSIGNED_BYTE);

					if (mode != 0) {
						packetWrapper.cancel();
						return;
					}

					packetWrapper.write(Type.INT, chunkX);
					packetWrapper.write(Type.INT, chunkZ);
					packetWrapper.write(Type.BOOLEAN, true);
					packetWrapper.write(Type.SHORT, (short) 0);
					packetWrapper.write(Type.SHORT, (short) 0);
					packetWrapper.write(Type.INT, 0);
				});
			}
		});

		this.registerClientbound(ClientboundPackets1_2_5.CHUNK_DATA, new PacketRemapper() {
			@Override
			public void registerMap() {
				map(Type.INT); // Chunk-X
				map(Type.INT); // Chunk-Z
				map(Type.BOOLEAN); // Ground up
				map(Type.SHORT); // Primary bitmask
				map(Type.SHORT); // Add bitmask
				map(Type.INT); // Data length
				map(Type.INT, Type.NOTHING); // Useless shit
			}
		});

		this.registerClientbound(ClientboundPackets1_2_5.EXPLOSION, new PacketRemapper() {
			@Override
			public void registerMap() {
				map(Type.DOUBLE); // X-Position
				map(Type.DOUBLE); // Y-Position
				map(Type.DOUBLE); // Z-Position
				map(Type.FLOAT); // Radius
				map(Type.INT); // Record count
				handler((pw) -> {
					int count = pw.get(Type.INT, 0);
					for (int i = 0; i < count; i++) {
						pw.passthrough(Type.BYTE); // X-Offset
						pw.passthrough(Type.BYTE); // Y-Offset
						pw.passthrough(Type.BYTE); // Z-Offset
					}
				});
				handler((pw) -> {
					pw.write(Type.FLOAT, 0.0F); // Player Motion X
					pw.write(Type.FLOAT, 0.0F); // Player Motion Y
					pw.write(Type.FLOAT, 0.0F); // Player Motion Z
				});
			}
		});

		this.registerClientbound(ClientboundPackets1_2_5.BLOCK_ACTION, new PacketRemapper() {
			@Override
			public void registerMap() {
				map(Type.INT);
				map(Type.SHORT);
				map(Type.INT);
				map(Type.BYTE);
				map(Type.BYTE);
				handler(packetWrapper -> {
					packetWrapper.write(Type.SHORT, (short) 25); //blockId
					packetWrapper.cancel();
				});
			}
		});

		this.registerClientbound(ClientboundPackets1_2_5.BLOCK_CHANGE, new PacketRemapper() {
			@Override
			public void registerMap() {
				map(Type.INT); // X-Position
				map(Type.UNSIGNED_BYTE); // Y-Position
				map(Type.INT); // Z-Position
				map(Type.BYTE, Type.SHORT); // Block-Type
				map(Type.BYTE); // Block-Metadata
			}
		});
	}

	@Override
	public void init(UserConnection connection) {
		super.init(connection);

		connection.put(new LevelDataStorage(connection));
	}
}
