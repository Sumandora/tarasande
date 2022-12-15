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

package de.florianmichael.vialegacy.protocols.protocol1_4_6_7to1_4_4_5;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;
import de.florianmichael.vialegacy.protocol.SplitterTracker;
import de.florianmichael.vialegacy.protocols.protocol1_4_6_7to1_4_4_5.type.impl.ChunkBulk1_4_4_5Type;
import de.florianmichael.vialegacy.protocols.base.ClientboundLoginPackets1_6_4;
import de.florianmichael.vialegacy.protocols.protocol1_8_0_9to1_7_6_10.type.Types1_7_6_10;
import de.florianmichael.vialegacy.protocols.protocol1_7_0_5to1_6_4.type.Types1_6_4;
import de.florianmichael.vialegacy.protocols.protocol1_7_0_5to1_6_4.type.MetaType_1_6_4;
import de.florianmichael.vialegacy.api.EnZaProtocol;
import de.florianmichael.vialegacy.protocols.protocol1_4_6_7to1_4_4_5.storage.DimensionStorage;
import de.florianmichael.vialegacy.protocols.protocol1_5_1to1_4_6_7.ClientboundPackets1_4_6_7;
import de.florianmichael.vialegacy.protocols.protocol1_5_1to1_4_6_7.ServerboundPackets1_4_6_7;
import de.florianmichael.vialegacy.protocols.protocol1_8_0_9to1_7_6_10.type.impl.ChunkBulk1_7_6_10Type;

import java.util.ArrayList;
import java.util.List;

public class Protocol1_4_6_7to1_4_4_5 extends EnZaProtocol<ClientboundPackets1_4_4_5, ClientboundPackets1_4_6_7, ServerboundPackets1_4_4_5, ServerboundPackets1_4_6_7> {

	public Protocol1_4_6_7to1_4_4_5() {
		super(ClientboundPackets1_4_4_5.class, ClientboundPackets1_4_6_7.class, ServerboundPackets1_4_4_5.class, ServerboundPackets1_4_6_7.class);
	}

	@Override
	protected void registerPackets() {
		super.registerPackets();

		this.registerClientbound(ClientboundPackets1_4_4_5.JOIN_GAME, new PacketRemapper() {
			@Override
			public void registerMap() {
				map(Type.INT); // Entity-Id
				map(Types1_6_4.STRING); // Level-Type
				map(Type.BYTE); // GameMode
				map(Type.BYTE); // Dimension
				map(Type.BYTE); // Difficulty
				map(Type.BYTE); // WorldHeight
				map(Type.BYTE); // Max. Players
				handler((pw) -> {
					//noinspection ConstantConditions
					pw.user().get(DimensionStorage.class).setDimension(pw.get(Type.BYTE, 1));
				});
			}
		});

		this.registerClientbound(ClientboundPackets1_4_4_5.RESPAWN, new PacketRemapper() {
			@Override
			public void registerMap() {
				map(Type.INT); // Dimension
				map(Type.BYTE); // Difficulty
				map(Type.BYTE); // GameMode
				map(Type.SHORT); // WorldHeight
				map(Types1_6_4.STRING); // Level-Type
				handler((pw) -> {
					//noinspection ConstantConditions
					pw.user().get(DimensionStorage.class).setDimension(pw.get(Type.INT, 0));
				});
			}
		});

		this.registerClientbound(ClientboundPackets1_4_4_5.PICKUP_ITEM, ClientboundPackets1_4_6_7.SPAWN_ENTITY, new PacketRemapper() {
			@Override
			public void registerMap() {
				handler((pw) -> {
					final int entityID = pw.read(Type.INT);
					final Item item = pw.read(Types1_7_6_10.COMPRESSED_NBT_ITEM);

					final int x = pw.read(Type.INT);
					final int y = pw.read(Type.INT);
					final int z = pw.read(Type.INT);

					byte yaw = pw.read(Type.BYTE);
					byte pitch = pw.read(Type.BYTE);
					byte roll = pw.read(Type.BYTE);

					pw.clearPacket();

					pw.write(Type.INT, entityID); // Entity-Id

					pw.write(Type.BYTE, (byte) 2); // Type of Item

					pw.write(Type.INT, x); // X-Position
					pw.write(Type.INT, y); // Y-Position
					pw.write(Type.INT, z); // Z-Position

					pw.write(Type.BYTE, (byte) 0); // Yaw
					pw.write(Type.BYTE, (byte) 0); // Pitch

					pw.write(Type.INT, 0); // ThrowerEntity-Id

					pw.send(Protocol1_4_6_7to1_4_4_5.class);

					final PacketWrapper metadataPacket = PacketWrapper.create(ClientboundPackets1_4_4_5.ENTITY_METADATA, pw.user());
					metadataPacket.write(Type.INT, entityID);
					List<Metadata> metas = new ArrayList<>();
					metas.add(new Metadata(10, MetaType_1_6_4.Slot, item));
					metadataPacket.write(Types1_6_4.METADATA_LIST, metas);
					metadataPacket.send(Protocol1_4_6_7to1_4_4_5.class);
				});
			}
		});

		this.registerClientbound(ClientboundPackets1_4_4_5.SPAWN_ENTITY, new PacketRemapper() {
			@Override
			public void registerMap() {
				map(Type.INT); // Entity-Id

				map(Type.BYTE); // Entity-Type

				map(Type.INT); // X-Position
				map(Type.INT); // Y-Position
				map(Type.INT); // Z-Position

				handler((pw) -> pw.write(Type.BYTE, (byte) 0)); // Yaw
				handler((pw) -> pw.write(Type.BYTE, (byte) 0)); // Pitch
			}
		});

		this.registerClientbound(ClientboundPackets1_4_4_5.MAP_BULK_CHUNK, new PacketRemapper() {
			@Override
			public void registerMap() {
				handler(wrapper -> {
					final ClientWorld world = wrapper.user().get(ClientWorld.class);

					final Chunk[] chunks = wrapper.read(new ChunkBulk1_4_4_5Type(world, wrapper.user().get(DimensionStorage.class).getDimension() == 0));
					wrapper.write(new ChunkBulk1_7_6_10Type(world), chunks);
				});
			}
		});

		this.registerServerbound(ServerboundPackets1_4_6_7.PLAYER_DIGGING, new PacketRemapper() {
			@Override
			public void registerMap() {
				map(Type.UNSIGNED_BYTE); // Status
				map(Type.INT); // X-Position
				map(Type.UNSIGNED_BYTE); // Y-Position
				map(Type.INT); // Z-Position
				map(Type.UNSIGNED_BYTE); // Face

				handler((pw) -> {
					int status = pw.get(Type.UNSIGNED_BYTE, 0);

					if (status == 3) {
						status = 4;
						pw.set(Type.UNSIGNED_BYTE, 0, (short) status);
					}
				});
			}
		});
	}

	@Override
	public void init(UserConnection connection) {
		super.init(connection);

		connection.put(new DimensionStorage(connection));
		connection.put(new SplitterTracker(connection, ClientboundPackets1_4_4_5.values(), ClientboundLoginPackets1_6_4.values()));

		if (!connection.has(ClientWorld.class)) {
			connection.put(new ClientWorld(connection));
		}
	}
}
