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

import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.vialegacy.api.via.EnZaProtocol;
import de.florianmichael.vialegacy.protocols.protocol1_6_2to1_6_1.ClientboundPackets1_6_1;
import de.florianmichael.vialegacy.protocols.protocol1_6_2to1_6_1.ServerboundPackets1_6_1;

public class Protocol1_6_1to1_5_2 extends EnZaProtocol<ClientboundPackets1_5_2, ClientboundPackets1_6_1, ServerboundPackets1_5_2, ServerboundPackets1_6_1> {

	public Protocol1_6_1to1_5_2() {
		super(ClientboundPackets1_5_2.class, ClientboundPackets1_6_1.class, ServerboundPackets1_5_2.class, ServerboundPackets1_6_1.class);
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

		this.registerClientbound(ClientboundPackets1_5_2.ATTACH_ENTITY, new PacketRemapper() {

			@Override
			public void registerMap() {
				map(Type.INT); // Entity-Id
				map(Type.INT); // Vehicle-Id
				handler((pw) -> pw.write(Type.UNSIGNED_BYTE, (short) 0));
			}
		});

		this.cancelClientbound(ClientboundPackets1_5_2.ENTITY_PROPERTIES);

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
}
