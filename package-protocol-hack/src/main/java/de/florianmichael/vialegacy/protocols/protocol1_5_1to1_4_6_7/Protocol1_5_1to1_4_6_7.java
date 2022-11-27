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

package de.florianmichael.vialegacy.protocols.protocol1_5_1to1_4_6_7;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.vialegacy.protocol.SplitterTracker;
import de.florianmichael.vialegacy.protocols.protocol1_7_0_5to1_6_4.ClientboundLoginPackets1_6_4;
import de.florianmichael.vialegacy.protocols.protocol1_7_0_5to1_6_4.type.Types1_6_4;
import de.florianmichael.vialegacy.api.EnZaProtocol;
import de.florianmichael.vialegacy.protocols.protocol1_5_2to1_5_1.ClientboundPackets1_5_1;
import de.florianmichael.vialegacy.protocols.protocol1_5_2to1_5_1.ServerboundPackets1_5_1;

public class Protocol1_5_1to1_4_6_7 extends EnZaProtocol<ClientboundPackets1_4_6_7, ClientboundPackets1_5_1, ServerboundPackets1_4_6_7, ServerboundPackets1_5_1> {

	public Protocol1_5_1to1_4_6_7() {
		super(ClientboundPackets1_4_6_7.class, ClientboundPackets1_5_1.class, ServerboundPackets1_4_6_7.class, ServerboundPackets1_5_1.class);
	}

	@Override
	protected void registerPackets() {
		super.registerPackets();

		// Scoreboards were added in 1.5.1
		this.cancelClientbound(ClientboundPackets1_4_6_7.SCOREBOARD_OBJECTIVE);
		this.cancelClientbound(ClientboundPackets1_4_6_7.UPDATE_SCORE);
		this.cancelClientbound(ClientboundPackets1_4_6_7.DISPLAY_SCOREBOARD);
		this.cancelClientbound(ClientboundPackets1_4_6_7.TEAMS);

		this.cancelClientbound(ClientboundPackets1_4_6_7.OPEN_SIGN_EDITOR);

		this.registerClientbound(ClientboundPackets1_4_6_7.OPEN_WINDOW, new PacketRemapper() {
			@Override
			public void registerMap() {
				map(Type.BYTE); // Window-Id
				map(Type.BYTE); // Inventory-Type

				map(Types1_6_4.STRING); // Window-Title

				map(Type.BYTE); // Slot count
				handler((pw) -> pw.write(Type.BOOLEAN, false));
			}
		});

		this.registerClientbound(ClientboundPackets1_4_6_7.SPAWN_ENTITY, new PacketRemapper() {
			@Override
			public void registerMap() {
				map(Type.INT); // Entity ID
				map(Type.BYTE); // Vehicle Type
				map(Type.INT); // X
				map(Type.INT); // Y
				map(Type.INT); // Z
				map(Type.BYTE); // Yaw
				map(Type.BYTE); // Pitch
				map(Type.INT); // Thrower ID

				handler(wrapper -> {
					final byte vehicleType = wrapper.get(Type.BYTE, 0);

					switch (vehicleType) {
						case (byte) 10: // Rideable MineCart
							wrapper.set(Type.INT, 4, 0); // Thrower ID
							break;
						case (byte) 11: // Chest MineCart
							wrapper.set(Type.BYTE, 0, (byte) 10); // Vehicle Type
							wrapper.set(Type.INT, 4, 1); // Thrower ID
							break;
						case (byte) 12: // Furnace MineCart
							wrapper.set(Type.BYTE, 0, (byte) 10); // Vehicle Type
							wrapper.set(Type.INT, 4, 2); // Thrower ID
							break;
					}
				});
			}
		});

		this.registerServerbound(ServerboundPackets1_5_1.CLICK_WINDOW, new PacketRemapper() {
			@Override
			public void registerMap() {
				map(Type.BYTE); // Window ID
				map(Type.SHORT); // Slot
				map(Type.UNSIGNED_BYTE); // Button
				map(Type.SHORT); // Action Number
				map(Type.UNSIGNED_BYTE); // Inventory Action
				handler(wrapper -> {
					final short slot = wrapper.get(Type.SHORT, 0);
					final short button = wrapper.get(Type.UNSIGNED_BYTE, 0);
					final short inventoryAction = wrapper.get(Type.UNSIGNED_BYTE, 1);

					boolean leftClickFlag = false;
					boolean startDragging = false;
					boolean endDragging = false;
					boolean droppingUsingQ = false;
					boolean addSlot = false;

					switch (inventoryAction) {
						case 0:
							leftClickFlag = button == 0;
							break;
						case 4:
							droppingUsingQ = button + (slot != -999 ? 2 : 0) == 2;
							break;
						case 5:
							startDragging = button == 0;
							endDragging = button == 2;
							addSlot = button == 1;
							break;
					}

					boolean leftClick = leftClickFlag || startDragging || addSlot || endDragging;
					boolean clickingOutside = slot == -999 && inventoryAction != 5;
					boolean usingShift = inventoryAction == 1;

					int mouseClick = leftClick ? 0 : 1;

					if (droppingUsingQ) {
						final PacketWrapper closeWindow = PacketWrapper.create(ClientboundPackets1_4_6_7.CLOSE_WINDOW, wrapper.user());
						closeWindow.write(Type.BYTE, (byte) 0);
						closeWindow.send(Protocol1_5_1to1_4_6_7.class);

						wrapper.cancel();
						return;
					}

					if (slot < 0 && !clickingOutside) {
						wrapper.cancel();
						return;
					}

					wrapper.set(Type.UNSIGNED_BYTE, 0, (short) mouseClick);
					wrapper.set(Type.UNSIGNED_BYTE, 1, (short) (usingShift ? 1 : 0));
				});
			}
		});
	}

	@Override
	public void init(UserConnection connection) {
		super.init(connection);

		connection.put(new SplitterTracker(connection, ClientboundPackets1_4_6_7.values(), ClientboundLoginPackets1_6_4.values()));
	}
}
