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

package de.florianmichael.vialegacy.protocols.protocol1_4_4_5to1_4_3_pre;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.vialegacy.protocol.SplitterTracker;
import de.florianmichael.vialegacy.protocols.protocol1_4_4_5to1_4_3_pre.type.Types1_4_2;
import de.florianmichael.vialegacy.protocols.base.ClientboundLoginPackets1_6_4;
import de.florianmichael.vialegacy.protocols.protocol1_7_0_5to1_6_4.type.Types1_6_4;
import de.florianmichael.vialegacy.api.EnZaProtocol;
import de.florianmichael.vialegacy.protocols.protocol1_4_6_7to1_4_4_5.ClientboundPackets1_4_4_5;
import de.florianmichael.vialegacy.protocols.protocol1_4_6_7to1_4_4_5.ServerboundPackets1_4_4_5;

public class Protocol1_4_5to1_4_3_pre extends EnZaProtocol<ClientboundPackets1_4_3_pre, ClientboundPackets1_4_4_5, ServerboundPackets1_4_3_pre, ServerboundPackets1_4_4_5> {

	public Protocol1_4_5to1_4_3_pre() {
		super(ClientboundPackets1_4_3_pre.class, ClientboundPackets1_4_4_5.class, ServerboundPackets1_4_3_pre.class, ServerboundPackets1_4_4_5.class);
	}

	@Override
	protected void registerPackets() {
		super.registerPackets();

		this.registerClientbound(ClientboundPackets1_4_3_pre.SPAWN_PLAYER, new PacketRemapper() {
			@Override
			public void registerMap() {
				map(Type.INT); // Entity-Id

				map(Types1_6_4.STRING); // Player name

				map(Type.INT); // X-Position
				map(Type.INT); // Y-Position
				map(Type.INT); // Z-Position

				map(Type.BYTE); // Yaw
				map(Type.BYTE); // Pitch

				map(Type.SHORT); // Current item

				map(Types1_4_2.METADATA_LIST, Types1_6_4.METADATA_LIST); // Metadata list
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

				map(Types1_4_2.METADATA_LIST, Types1_6_4.METADATA_LIST); // Metadata list
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
				map(Types1_4_2.METADATA_LIST, Types1_6_4.METADATA_LIST); // Metadata list
			}
		});
	}

	@Override
	public void init(UserConnection connection) {
		super.init(connection);

		connection.put(new SplitterTracker(connection, ClientboundPackets1_4_3_pre.values(), ClientboundLoginPackets1_6_4.values()));
	}
}
