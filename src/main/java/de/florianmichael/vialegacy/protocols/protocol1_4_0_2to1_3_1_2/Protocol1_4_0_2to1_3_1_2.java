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

package de.florianmichael.vialegacy.protocols.protocol1_4_0_2to1_3_1_2;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.item.DataItem;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.vialegacy.api.sound.SoundRewriter;
import de.florianmichael.vialegacy.protocol.SplitterTracker;
import de.florianmichael.vialegacy.protocols.protocol1_4_0_2to1_3_1_2.sound.SoundRewriter1_4_0_2to1_3_1_2;
import de.florianmichael.vialegacy.protocols.protocol1_7_0_5to1_6_4.ClientboundLoginPackets1_6_4;
import de.florianmichael.vialegacy.protocols.protocol1_8_0_9to1_7_6_10.type.Types1_7_6_10;
import de.florianmichael.vialegacy.protocols.protocol1_4_4_5to1_4_3_pre.type.TypeRegistry_1_4_2;
import de.florianmichael.vialegacy.protocols.protocol1_7_0_5to1_6_4.type.TypeRegistry_1_6_4;
import de.florianmichael.vialegacy.api.EnZaProtocol;
import de.florianmichael.vialegacy.protocols.protocol1_4_3_preto1_4_0_2.ClientboundPackets1_4_0_2;
import de.florianmichael.vialegacy.protocols.protocol1_4_3_preto1_4_0_2.ServerboundPackets1_4_0_2;

public class Protocol1_4_0_2to1_3_1_2 extends EnZaProtocol<ClientboundPackets1_3_1_2, ClientboundPackets1_4_0_2, ServerboundPackets1_3_1_2, ServerboundPackets1_4_0_2> {

	private final SoundRewriter<Protocol1_4_0_2to1_3_1_2> soundRewriter = new SoundRewriter1_4_0_2to1_3_1_2(this);

	public Protocol1_4_0_2to1_3_1_2() {
		super(ClientboundPackets1_3_1_2.class, ClientboundPackets1_4_0_2.class, ServerboundPackets1_3_1_2.class, ServerboundPackets1_4_0_2.class);
	}

	@Override
	protected void registerPackets() {
		super.registerPackets();
		this.soundRewriter().registerNamedSound(ClientboundPackets1_3_1_2.NAMED_SOUND);

		this.registerClientbound(ClientboundPackets1_3_1_2.PICKUP_ITEM, new PacketRemapper() {
			@Override
			public void registerMap() {
				map(Type.INT); // Entity-Id
				handler(pw -> {
					final short id = pw.read(Type.SHORT); // Item-ID
					final byte count = pw.read(Type.BYTE); // Amount
					final short metadata = pw.read(Type.SHORT); // Item-Damage

					pw.write(Types1_7_6_10.COMPRESSED_NBT_ITEM, new DataItem(id, count, metadata, null));
				});
				map(Type.INT); // X-Position
				map(Type.INT); // Y-Position
				map(Type.INT); // Z-Position
				map(Type.BYTE); // Yaw
				map(Type.BYTE); // Pitch
				map(Type.BYTE); // Roll
			}
		});

		this.registerClientbound(ClientboundPackets1_3_1_2.SPAWN_MOB, new PacketRemapper() {
			@Override
			public void registerMap() {
				//noinspection DuplicatedCode
				map(Type.INT); // Entity-Id

				map(Type.BYTE); // Type

				map(Type.INT); // X-Position
				map(Type.INT); // Y-Position
				map(Type.INT); // Z-Position

				map(Type.BYTE); // Pitch
				map(Type.BYTE); // Head pitch
				map(Type.BYTE); // Yaw

				map(Type.SHORT); // Velocity-X
				map(Type.SHORT); // Velocity-Y
				map(Type.SHORT); // Velocity-Z

				map(TypeRegistry_1_4_2.METADATA_LIST); // Metadata list

				handler((pw) -> {
					final int type = pw.get(Type.BYTE, 0);
					int itemId = 0;

					if (type == 51) {
						itemId = 261; // Bow
					}
					if (type == 57) {
						itemId = 283; // Golden Sword
					}

					if (itemId > 0) {
						final PacketWrapper entityEquipment = PacketWrapper.create(ClientboundPackets1_3_1_2.ENTITY_EQUIPMENT, pw.user());

						entityEquipment.write(Type.INT, pw.get(Type.INT, 0));
						entityEquipment.write(Type.SHORT, (short) 0);
						entityEquipment.write(Types1_7_6_10.COMPRESSED_NBT_ITEM, new DataItem(itemId, (byte) 1, (short) 0, null));

						entityEquipment.send(Protocol1_4_0_2to1_3_1_2.class);
					}
				});
			}
		});

		this.registerClientbound(ClientboundPackets1_3_1_2.TIME_UPDATE, new PacketRemapper() {
			@Override
			public void registerMap() {
				map(Type.LONG); // Age of world
				handler((pw) -> pw.write(Type.LONG, pw.get(Type.LONG, 0)));
			}
		});

		this.registerClientbound(ClientboundPackets1_3_1_2.EFFECT, new PacketRemapper() {
			@Override
			public void registerMap() {
				map(Type.INT); // Effect-Id
				map(Type.INT); // X-Position

				map(Type.UNSIGNED_BYTE); // Y-Position

				map(Type.INT); // Z-Position
				map(Type.INT); // Aux-Data

				handler((pw) -> pw.write(Type.BOOLEAN, false));
			}
		});

		this.registerClientbound(ClientboundPackets1_3_1_2.MAP_DATA, new PacketRemapper() {
			@Override
			public void registerMap() {
				map(Type.SHORT);
				map(Type.SHORT);
				handler(wrapper -> {
					// TODO | Rewrite to own Type if needed
					final byte length = wrapper.read(Type.BYTE);
					final byte[] mapData = new byte[length];
					for (int i = 0; i < length; i++) {
						mapData[i] = wrapper.read(Type.BYTE);
					}

					if (mapData[0] == 1) {
						for (int i = 0; i < (mapData.length - 1) / 3; ++i) {
							final byte icon = (byte) (mapData[i * 3 + 1] % 16);
							final byte centerX = mapData[i * 3 + 2];
							final byte centerZ = mapData[i * 3 + 3];
							final byte iconRotation = (byte) (mapData[i * 3 + 1] / 16);
							mapData[i * 3 + 1] = (byte) (icon << 4 | iconRotation & 15);
							mapData[i * 3 + 2] = centerX;
							mapData[i * 3 + 3] = centerZ;
						}
					}
				});
			}
		});

		this.registerClientbound(ClientboundPackets1_3_1_2.EFFECT, new PacketRemapper() {
			@Override
			public void registerMap() {
				map(Type.INT); // Effect ID
				map(Type.INT); // X
				map(Type.UNSIGNED_BYTE); // Y
				map(Type.INT); // Z
				map(Type.INT); // data
				handler(wrapper -> wrapper.write(Type.BOOLEAN, false)); // Disable Relative Volume
			}
		});

		this.registerServerbound(ServerboundPackets1_4_0_2.CLIENT_SETTINGS, new PacketRemapper() {
			@Override
			public void registerMap() {
				map(TypeRegistry_1_6_4.STRING); // Locale

				map(Type.BYTE); // View Distance
				map(Type.BYTE); // Chat flags

				map(Type.BYTE); // Difficulty
				map(Type.BOOLEAN, Type.NOTHING); // Show Cape
			}
		});
	}

	@Override
	public SoundRewriter<Protocol1_4_0_2to1_3_1_2> soundRewriter() {
		return this.soundRewriter;
	}

	@Override
	public void init(UserConnection connection) {
		super.init(connection);

		connection.put(new SplitterTracker(connection, ClientboundPackets1_3_1_2.values(), ClientboundLoginPackets1_6_4.values()));
	}
}
