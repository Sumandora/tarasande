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

package de.florianmichael.vialegacy.protocols.protocol1_6_2to1_6_1;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocol.packet.PacketWrapperImpl;
import de.florianmichael.vialegacy.api.type.TypeRegistry1_7_6_10;
import de.florianmichael.vialegacy.api.type.TypeRegistry_1_6_4;
import de.florianmichael.vialegacy.api.via.EnZaProtocol;
import de.florianmichael.vialegacy.protocols.protocol1_6_2to1_6_1.storage.BlockPlaceTracker;
import de.florianmichael.vialegacy.protocols.protocol1_6_2to1_6_1.util.EnumFace;
import de.florianmichael.vialegacy.protocols.protocol1_6_3to1_6_2.ClientboundPackets1_6_2;
import de.florianmichael.vialegacy.protocols.protocol1_6_3to1_6_2.ServerboundPackets1_6_2;
import de.florianmichael.vialegacy.protocols.protocol1_7_5to1_6_4.ClientboundPackets1_6_4;
import de.florianmichael.vialegacy.protocols.protocol1_7_5to1_6_4.ServerboundPackets1_6_4;
import de.florianmichael.vialegacy.protocols.protocol1_7_5to1_6_4.entity.EntityAttributeModifier;
import de.florianmichael.vialegacy.protocols.protocol1_7_5to1_6_4.entity.EntityProperty;
import de.florianmichael.vialegacy.protocols.protocol1_8to1_7_10.ClientboundPackets1_7_10;
import de.florianmichael.vialegacy.protocols.protocol1_8to1_7_10.ServerboundPackets1_7_10;

import java.util.ArrayList;
import java.util.List;

public class Protocol1_6_2to1_6_1 extends EnZaProtocol<ClientboundPackets1_6_1, ClientboundPackets1_6_2, ServerboundPackets1_6_1, ServerboundPackets1_6_2> {

	public Protocol1_6_2to1_6_1() {
		super(ClientboundPackets1_6_1.class, ClientboundPackets1_6_2.class, ServerboundPackets1_6_1.class, ServerboundPackets1_6_2.class);
	}

	@Override
	protected void registerPackets() {
		this.cancelClientbound(ClientboundPackets1_6_1.CREATIVE_INVENTORY_ACTION);

		this.registerServerbound(ServerboundPackets1_6_2.PLAYER_BLOCK_PLACEMENT, new PacketRemapper() {

			@Override
			public void registerMap() {
				map(Type.INT); // X-Position
				map(Type.UNSIGNED_BYTE); // Y-Position
				map(Type.INT); // Z-Position

				map(Type.UNSIGNED_BYTE); // Direction

				map(TypeRegistry1_7_6_10.COMPRESSED_NBT_ITEM); // Slot

				map(Type.UNSIGNED_BYTE); // Cursor X
				map(Type.UNSIGNED_BYTE); // Cursor Y
				map(Type.UNSIGNED_BYTE); // Cursor Z
			}
		});

		this.registerClientbound(ClientboundPackets1_6_1.BLOCK_CHANGE, new PacketRemapper() {

			@Override
			public void registerMap() {
				map(Type.INT); // X-Position
				map(Type.UNSIGNED_BYTE); // Y-Position
				map(Type.INT); // Z-Position

				map(Type.SHORT); // Block-Type
				map(Type.BYTE); // Block Metadata
			}
		});

		this.registerClientbound(ClientboundPackets1_6_1.ENTITY_PROPERTIES, new PacketRemapper() {

			@Override
			public void registerMap() {
				handler((pw) -> {
					final int entityId = pw.read(Type.INT);
					final int count = pw.read(Type.INT);

					final List<EntityProperty> list = new ArrayList<>(count);

					for (int i = 0; i < count; i++) {
						final EntityProperty prop = new EntityProperty();

						prop.key = pw.read(TypeRegistry_1_6_4.STRING);
						prop.value = pw.read(Type.DOUBLE); // Value
						prop.modifiers = new ArrayList<>();

						list.add(prop);
					}

					pw.clearPacket();

					pw.write(Type.INT, entityId);
					pw.write(Type.INT, list.size());

					for (EntityProperty prop : list) {
						pw.write(TypeRegistry_1_6_4.STRING, prop.key);
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
	}
	
	@Override
	public void init(UserConnection userConnection) {
		userConnection.put(new BlockPlaceTracker(userConnection));
	}
}
