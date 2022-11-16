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

package de.florianmichael.vialegacy.protocols.protocol1_6_1to1_5_2;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_10Types;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.util.Pair;
import de.florianmichael.vialegacy.api.EnZaProtocol;
import de.florianmichael.vialegacy.api.metadata.LegacyMetadataRewriter;
import de.florianmichael.vialegacy.api.sound.SoundRewriter;
import de.florianmichael.vialegacy.protocol.SplitterTracker;
import de.florianmichael.vialegacy.protocols.protocol1_4_5to1_4_3_pre.type.TypeRegistry_1_4_2;
import de.florianmichael.vialegacy.protocols.protocol1_6_1to1_5_2.metadata.MetadataRewriter1_6_1to1_5_2;
import de.florianmichael.vialegacy.protocols.protocol1_6_1to1_5_2.sound.SoundRewriter1_6_1to1_5_2;
import de.florianmichael.vialegacy.protocols.protocol1_6_1to1_5_2.storage.EntityTracker;
import de.florianmichael.vialegacy.protocols.protocol1_6_1to1_5_2.storage.VehicleTracker;
import de.florianmichael.vialegacy.protocols.protocol1_6_2to1_6_1.ClientboundPackets1_6_1;
import de.florianmichael.vialegacy.protocols.protocol1_6_2to1_6_1.ServerboundPackets1_6_1;
import de.florianmichael.vialegacy.protocols.protocol1_6_4to1_6_3pre.ClientboundPackets1_6_3_pre;
import de.florianmichael.vialegacy.protocols.protocol1_7_5to1_6_4.ClientboundLoginPackets1_6_4;
import de.florianmichael.vialegacy.protocols.protocol1_7_5to1_6_4.type.TypeRegistry_1_6_4;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unchecked", "ConstantConditions"})
public class Protocol1_6_1to1_5_2 extends EnZaProtocol<ClientboundPackets1_5_2, ClientboundPackets1_6_1, ServerboundPackets1_5_2, ServerboundPackets1_6_1> {

	private final LegacyMetadataRewriter<Protocol1_6_1to1_5_2> metadataRewriter = new MetadataRewriter1_6_1to1_5_2(this);
	private final SoundRewriter soundRewriter = new SoundRewriter1_6_1to1_5_2();

	public Protocol1_6_1to1_5_2() {
		super(ClientboundPackets1_5_2.class, ClientboundPackets1_6_1.class, ServerboundPackets1_5_2.class, ServerboundPackets1_6_1.class);
	}

	private void resetPlayerSpeed(final UserConnection connection, final int entityId) throws Exception {
		final PacketWrapper entityProperties = PacketWrapper.create(ClientboundPackets1_5_2.ENTITY_PROPERTIES, connection);

		entityProperties.write(Type.INT, entityId);
		entityProperties.write(Type.INT, 1); // Count

		entityProperties.write(TypeRegistry_1_6_4.STRING, "generic.movementSpeed");
		entityProperties.write(Type.DOUBLE, 0.10000000149011612D);

		entityProperties.send(Protocol1_6_1to1_5_2.class);
	}

	@Override
	protected void registerPackets() {
		this.registerServerbound(ServerboundPackets1_6_1.ENTITY_ACTION, new PacketRemapper() {

			@Override
			public void registerMap() {
				map(Type.INT); // Entity-Id
				map(Type.BYTE); // State
				map(Type.INT, Type.NOTHING); // Remove JumpBoost
			}
		});

		this.registerServerbound(ServerboundPackets1_6_1.PLAYER_ABILITIES, new PacketRemapper() {

			@Override
			public void registerMap() {
				map(Type.BYTE); // Flags
				handler((pw) -> {
					pw.write(Type.BYTE, (byte) ((int) (pw.read(Type.FLOAT) * 255.0F) & 0xFF));
					pw.write(Type.BYTE, (byte) ((int) (pw.read(Type.FLOAT) * 255.0F) & 0xFF));
				});
			}
		});

		this.registerServerbound(ServerboundPackets1_6_1.STEER_VEHICLE, new PacketRemapper() {
			@Override
			public void registerMap() {
				map(Type.FLOAT); // Sideways
				map(Type.FLOAT); // Forwards
				handler(wrapper -> {
					final VehicleTracker vehicleTracker = wrapper.user().get(VehicleTracker.class);
					final boolean dismount = wrapper.read(Type.BOOLEAN);

					if (dismount && vehicleTracker.vehicleId != 999) {
						final int cachedId = vehicleTracker.vehicleId;
						vehicleTracker.vehicleId = -999;
						wrapper.cancel();

						final PacketWrapper interactEntity = PacketWrapper.create(ServerboundPackets1_6_1.INTERACT_ENTITY, wrapper.user());

						interactEntity.write(Type.INT, wrapper.user().get(EntityTracker.class).ownEntityId);
						interactEntity.write(Type.INT, cachedId);
						interactEntity.write(Type.BYTE, (byte) 0);

						interactEntity.send(Protocol1_6_1to1_5_2.class);
					}
				});
			}
		});

		this.registerClientbound(ClientboundPackets1_5_2.JOIN_GAME, new PacketRemapper() {
			@Override
			public void registerMap() {
				handler(wrapper -> {
					final int entityId = wrapper.passthrough(Type.INT);
					final EntityTracker tracker = wrapper.user().get(EntityTracker.class);

					tracker.track(entityId, Entity1_10Types.EntityType.ENTITY_HUMAN);
					tracker.ownEntityId = entityId;

					resetPlayerSpeed(wrapper.user(), entityId);
				});
			}
		});

		this.registerClientbound(ClientboundPackets1_5_2.RESPAWN, new PacketRemapper() {
			@Override
			public void registerMap() {
				handler(wrapper -> resetPlayerSpeed(wrapper.user(), wrapper.user().get(EntityTracker.class).ownEntityId));
			}
		});

		this.registerClientbound(ClientboundPackets1_5_2.SPAWN_MOB, new PacketRemapper() {
			@Override
			public void registerMap() {
				handler(wrapper -> {
					final EntityTracker tracker = wrapper.user().get(EntityTracker.class);
					final int entityId = wrapper.passthrough(Type.INT);

					wrapper.passthrough(Type.BYTE); // Type

					wrapper.passthrough(Type.INT); // X-Position
					wrapper.passthrough(Type.INT); // Y-Position
					wrapper.passthrough(Type.INT); // Z-Position

					wrapper.passthrough(Type.BYTE); // Pitch
					wrapper.passthrough(Type.BYTE); // Head pitch
					wrapper.passthrough(Type.BYTE); // Yaw

					wrapper.passthrough(Type.SHORT); // Velocity-X
					wrapper.passthrough(Type.SHORT); // Velocity-Y
					wrapper.passthrough(Type.SHORT); // Velocity-Z

					final Entity1_10Types.EntityType entityType = Entity1_10Types.getTypeFromId(wrapper.get(Type.BYTE, 0), false);

					tracker.track(entityId, entityType);
					final List<Metadata> oldMetadata = wrapper.read(TypeRegistry_1_4_2.METADATA_LIST);
					metadataRewriter().rewrite(entityType, oldMetadata);
					wrapper.write(TypeRegistry_1_4_2.METADATA_LIST, oldMetadata);
				});
			}
		});

		this.registerClientbound(ClientboundPackets1_5_2.DESTROY_ENTITIES, new PacketRemapper() {
			@Override
			public void registerMap() {
				handler(wrapper -> {
					int amount = wrapper.passthrough(Type.UNSIGNED_BYTE);

					for (int i = 0; i < amount; i++) {
						final int entityId = wrapper.passthrough(Type.INT);
						wrapper.user().get(EntityTracker.class).remove(entityId);
					}
				});
			}
		});

		this.registerClientbound(ClientboundPackets1_5_2.ENTITY_METADATA, new PacketRemapper() {
			@Override
			public void registerMap() {
				handler(wrapper -> {
					final EntityTracker tracker = wrapper.user().get(EntityTracker.class);
					final int entityId = wrapper.passthrough(Type.INT);

					final List<Metadata> oldMetadata = wrapper.read(TypeRegistry_1_6_4.METADATA_LIST);
					metadataRewriter().rewrite(tracker.get(entityId), oldMetadata);
					wrapper.write(TypeRegistry_1_6_4.METADATA_LIST, oldMetadata);
				});
			}
		});

		this.registerClientbound(ClientboundPackets1_5_2.SPAWN_PLAYER, new PacketRemapper() {
			@Override
			public void registerMap() {
				handler(wrapper -> {
					final EntityTracker tracker = wrapper.user().get(EntityTracker.class);
					final int entityId = wrapper.passthrough(Type.INT);

					wrapper.passthrough(Type.INT); // X-Position
					wrapper.passthrough(Type.INT); // Y-Position
					wrapper.passthrough(Type.INT); // Z-Position
					wrapper.passthrough(Type.BYTE); // Yaw
					wrapper.passthrough(Type.BYTE); // Pitch
					wrapper.passthrough(Type.SHORT); // Current Item
					tracker.track(entityId, Entity1_10Types.EntityType.ENTITY_HUMAN);

					final List<Metadata> oldMetadata = wrapper.read(TypeRegistry_1_6_4.METADATA_LIST);
					metadataRewriter().rewrite(tracker.get(entityId), oldMetadata);
					wrapper.write(TypeRegistry_1_6_4.METADATA_LIST, oldMetadata);
				});
			}
		});

		this.registerClientbound(ClientboundPackets1_5_2.SPAWN_ENTITY, new PacketRemapper() {
			@Override
			public void registerMap() {
				handler(wrapper -> {
					final EntityTracker tracker = wrapper.user().get(EntityTracker.class);

					final int entityId = wrapper.passthrough(Type.INT);
					System.out.println(entityId);
					final byte vehicleEntity = wrapper.passthrough(Type.BYTE);
					System.out.println(entityId + " " + vehicleEntity);


					switch (vehicleEntity) {
						case 0x01:
							tracker.track(entityId, Entity1_10Types.ObjectType.BOAT.getType());
							break;
						case 0x02:
							tracker.track(entityId, Entity1_10Types.ObjectType.ITEM.getType());
							break;
						case 0x46:
							tracker.track(entityId, Entity1_10Types.ObjectType.FALLING_BLOCK.getType());
							break;
						case 0x0A:
							tracker.track(entityId, Entity1_10Types.ObjectType.MINECART.getType());
							break;
						case 0x3E:
							tracker.track(entityId, Entity1_10Types.ObjectType.EGG.getType());
							break;
						case 0x3D:
							tracker.track(entityId, Entity1_10Types.ObjectType.SNOWBALL.getType());
							break;
						case 0x3C:
							tracker.track(entityId, Entity1_10Types.ObjectType.TIPPED_ARROW.getType());
							break;
						case 0x41:
							tracker.track(entityId, Entity1_10Types.ObjectType.ENDER_PEARL.getType());
							break;
						case 0x4B:
							tracker.track(entityId, Entity1_10Types.ObjectType.THROWN_EXP_BOTTLE.getType());
							break;
						case 0x48:
							tracker.track(entityId, Entity1_10Types.ObjectType.ENDER_SIGNAL.getType());
							break;
					}
				});
			}
		});

		this.registerClientbound(ClientboundPackets1_5_2.NAMED_SOUND, new PacketRemapper() {
			@Override
			public void registerMap() {
				handler(wrapper -> {
					String soundName = wrapper.read(TypeRegistry_1_6_4.STRING);
					soundRewriter().rewrite(soundName);
					if (soundName == null || soundName.isEmpty()) {
						wrapper.cancel();
					}
					wrapper.write(TypeRegistry_1_6_4.STRING, soundName);
				});
			}
		});

		this.registerClientbound(ClientboundPackets1_5_2.ATTACH_ENTITY, new PacketRemapper() {

			@Override
			public void registerMap() {
				map(Type.INT); // Entity-Id
				map(Type.INT); // Vehicle-Id
				handler((pw) -> pw.write(Type.UNSIGNED_BYTE, (short) 0));
				handler(wrapper -> {
					final VehicleTracker vehicleTracker = wrapper.user().get(VehicleTracker.class);
					final int vehicleId = wrapper.get(Type.INT, 1);

					if (vehicleId != 1) {
						vehicleTracker.vehicleId = vehicleId;
					}
				});
			}
		});

		this.registerClientbound(ClientboundPackets1_5_2.PLAYER_ABILITIES, new PacketRemapper() {

			@Override
			public void registerMap() {
				map(Type.BYTE); // Flags
				handler((pw) -> {
					pw.write(Type.FLOAT, pw.read(Type.BYTE) / 255.0F);
					pw.write(Type.FLOAT, pw.read(Type.BYTE) / 255.0F);
				});
			}
		});

		this.registerClientbound(ClientboundPackets1_5_2.UPDATE_HEALTH, new PacketRemapper() {

			@Override
			public void registerMap() {
				map(Type.SHORT, Type.FLOAT); // Health

				map(Type.SHORT); // Food
				map(Type.FLOAT); // Saturation
			}
		});

		this.registerClientbound(ClientboundPackets1_5_2.STATISTICS, new PacketRemapper() {

			@Override
			public void registerMap() {
				map(Type.INT); // Statistic-Id
				map(Type.BYTE, Type.INT); // Amount
			}
		});
	}

	@Override
	public LegacyMetadataRewriter<?> metadataRewriter() {
		return this.metadataRewriter;
	}

	@Override
	public SoundRewriter soundRewriter() {
		return this.soundRewriter;
	}

	@Override
	public void init(UserConnection connection) {
		super.init(connection);
		connection.put(new EntityTracker(connection));
		connection.put(new VehicleTracker(connection));

		connection.put(new SplitterTracker(connection, ClientboundPackets1_5_2.values(), ClientboundLoginPackets1_6_4.values()));
	}
}
