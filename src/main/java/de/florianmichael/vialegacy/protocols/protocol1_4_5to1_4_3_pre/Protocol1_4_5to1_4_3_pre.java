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

package de.florianmichael.vialegacy.protocols.protocol1_4_5to1_4_3_pre;

import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.vialegacy.api.type.TypeRegistry_1_6_4;
import de.florianmichael.vialegacy.api.type._1_4_2.TypeRegistry1_4_2;
import de.florianmichael.vialegacy.api.via.EnZaProtocol;
import de.florianmichael.vialegacy.protocols.protocol1_4_7to1_4_5.ClientboundPackets1_4_5;
import de.florianmichael.vialegacy.protocols.protocol1_4_7to1_4_5.ServerboundPackets1_4_5;

public class Protocol1_4_5to1_4_3_pre extends EnZaProtocol<ClientboundPackets1_4_3_pre, ClientboundPackets1_4_5, ServerboundPackets1_4_3_pre, ServerboundPackets1_4_5> {

	public Protocol1_4_5to1_4_3_pre() {
		super(ClientboundPackets1_4_3_pre.class, ClientboundPackets1_4_5.class, ServerboundPackets1_4_3_pre.class, ServerboundPackets1_4_5.class);
	}

	@Override
	protected void registerPackets() {
		super.registerPackets();

		this.registerClientbound(ClientboundPackets1_4_3_pre.SPAWN_PLAYER, new PacketRemapper() {
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

				map(TypeRegistry1_4_2.METADATA_LIST, TypeRegistry_1_6_4.METADATA_LIST); // Metadata list
			}
		});

		this.registerClientbound(ClientboundPackets1_4_3_pre.SPAWN_MOB, new PacketRemapper() {
			@Override
			public void registerMap() {
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

				map(TypeRegistry1_4_2.METADATA_LIST, TypeRegistry_1_6_4.METADATA_LIST); // Metadata list
			}
		});

		this.registerClientbound(ClientboundPackets1_4_3_pre.MAP_DATA, new PacketRemapper() {
			@Override
			public void registerMap() {
				map(Type.SHORT);
				map(Type.SHORT);
				map(Type.UNSIGNED_BYTE, Type.SHORT);
			}
		});

		this.registerClientbound(ClientboundPackets1_4_3_pre.ENTITY_METADATA, new PacketRemapper() {
			@Override
			public void registerMap() {
				map(Type.INT); // Entity-Id
				map(TypeRegistry1_4_2.METADATA_LIST, TypeRegistry_1_6_4.METADATA_LIST); // Metadata list
			}
		});
	}
}
