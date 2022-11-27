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

package de.florianmichael.vialegacy.protocols.protocol1_6_2to1_6_1;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.vialegacy.protocol.SplitterTracker;
import de.florianmichael.vialegacy.protocols.protocol1_7_0_5to1_6_4.ClientboundLoginPackets1_6_4;
import de.florianmichael.vialegacy.protocols.protocol1_8_0_9to1_7_6_10.type.Types1_7_6_10;
import de.florianmichael.vialegacy.protocols.protocol1_7_0_5to1_6_4.type.Types1_6_4;
import de.florianmichael.vialegacy.api.EnZaProtocol;
import de.florianmichael.vialegacy.protocols.protocol1_6_3to1_6_2.ClientboundPackets1_6_2;
import de.florianmichael.vialegacy.protocols.protocol1_6_3to1_6_2.ServerboundPackets1_6_2;
import de.florianmichael.vialegacy.protocols.protocol1_7_0_5to1_6_4.model.EntityAttributeModifier;
import de.florianmichael.vialegacy.protocols.protocol1_7_0_5to1_6_4.model.EntityProperty;

import java.util.ArrayList;
import java.util.List;

public class Protocol1_6_2to1_6_1 extends EnZaProtocol<ClientboundPackets1_6_1, ClientboundPackets1_6_2, ServerboundPackets1_6_1, ServerboundPackets1_6_2> {

	public Protocol1_6_2to1_6_1() {
		super(ClientboundPackets1_6_1.class, ClientboundPackets1_6_2.class, ServerboundPackets1_6_1.class, ServerboundPackets1_6_2.class);
	}

	@Override
	protected void registerPackets() {
		this.registerClientbound(ClientboundPackets1_6_1.ENTITY_PROPERTIES, new PacketRemapper() {

			@Override
			public void registerMap() {
				handler((pw) -> {
					final int entityId = pw.read(Type.INT);
					final int count = pw.read(Type.INT);

					final List<EntityProperty> list = new ArrayList<>(count);

					for (int i = 0; i < count; i++) {
						final EntityProperty prop = new EntityProperty();

						prop.key = pw.read(Types1_6_4.STRING);
						prop.value = pw.read(Type.DOUBLE); // Value
						prop.modifiers = new ArrayList<>();

						list.add(prop);
					}

					pw.clearPacket();

					pw.write(Type.INT, entityId);
					pw.write(Type.INT, list.size());

					for (EntityProperty prop : list) {
						pw.write(Types1_6_4.STRING, prop.key);
						pw.write(Type.DOUBLE, prop.value);
						pw.write(Type.SHORT, (short) prop.modifiers.size());

						for (int k = 0; k < prop.modifiers.size(); k++) {
							final EntityAttributeModifier mod = prop.modifiers.get(k);

							pw.write(Type.UUID, mod.uuid);
							pw.write(Type.DOUBLE, mod.amount);
							pw.write(Type.BYTE, mod.operation);
						}
					}
				});
			}
		});

		this.registerServerbound(ServerboundPackets1_6_2.PLAYER_BLOCK_PLACEMENT, new PacketRemapper() {
			@Override
			public void registerMap() {
				handler(wrapper -> {
					int x = wrapper.passthrough(Type.INT);
					int y = wrapper.passthrough(Type.UNSIGNED_BYTE);
					int z = wrapper.passthrough(Type.INT);

					final int direction = wrapper.passthrough(Type.UNSIGNED_BYTE);
					final Item item = wrapper.passthrough(Types1_7_6_10.COMPRESSED_NBT_ITEM);

					if (item == null) {
						return;
					}

					if (direction == 1) {
						++y;
					} else if (direction == 2) {
						--z;
					} else if (direction == 3) {
						++z;
					} else if (direction == 4) {
						--x;
					} else if (direction == 5) {
						++x;
					}

					if (item.identifier() == 323 && wrapper.passthrough(Type.UNSIGNED_BYTE) > 5) { // Sign and Cursor X
						wrapper.cancel();

						final PacketWrapper openSignEditor = PacketWrapper.create(ClientboundPackets1_6_1.OPEN_SIGN_EDITOR, wrapper.user());
						openSignEditor.write(Type.BYTE, (byte) 0);

						openSignEditor.write(Type.INT, x);
						openSignEditor.write(Type.INT, y);
						openSignEditor.write(Type.INT, z);

						openSignEditor.send(Protocol1_6_2to1_6_1.class);
					}
				});
			}
		});
	}

	@Override
	public void init(UserConnection connection) {
		super.init(connection);

		connection.put(new SplitterTracker(connection, ClientboundPackets1_6_1.values(), ClientboundLoginPackets1_6_4.values()));
	}
}
