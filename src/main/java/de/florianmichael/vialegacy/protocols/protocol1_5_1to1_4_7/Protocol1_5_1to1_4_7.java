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

package de.florianmichael.vialegacy.protocols.protocol1_5_1to1_4_7;

import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.vialegacy.protocols.protocol1_7_5to1_6_4.type.TypeRegistry_1_6_4;
import de.florianmichael.vialegacy.api.EnZaProtocol;
import de.florianmichael.vialegacy.protocols.protocol1_5_2to1_5_1.ClientboundPackets1_5_1;
import de.florianmichael.vialegacy.protocols.protocol1_5_2to1_5_1.ServerboundPackets1_5_1;

public class Protocol1_5_1to1_4_7 extends EnZaProtocol<ClientboundPackets1_4_7, ClientboundPackets1_5_1, ServerboundPackets1_4_7, ServerboundPackets1_5_1> {

	public Protocol1_5_1to1_4_7() {
		super(ClientboundPackets1_4_7.class, ClientboundPackets1_5_1.class, ServerboundPackets1_4_7.class, ServerboundPackets1_5_1.class);
	}

	@Override
	protected void registerPackets() {
		super.registerPackets();

		// Scoreboards were added in 1.5.1
		this.cancelClientbound(ClientboundPackets1_4_7.SCOREBOARD_OBJECTIVE);
		this.cancelClientbound(ClientboundPackets1_4_7.UPDATE_SCORE);
		this.cancelClientbound(ClientboundPackets1_4_7.DISPLAY_SCOREBOARD);
		this.cancelClientbound(ClientboundPackets1_4_7.TEAMS);

		this.cancelClientbound(ClientboundPackets1_4_7.OPEN_SIGN_EDITOR);

		this.registerClientbound(ClientboundPackets1_4_7.OPEN_WINDOW, new PacketRemapper() {
			@Override
			public void registerMap() {
				map(Type.BYTE); // Window-Id
				map(Type.BYTE); // Inventory-Type

				map(TypeRegistry_1_6_4.STRING); // Window-Title

				map(Type.BYTE); // Slot count
				handler((pw) -> pw.write(Type.BOOLEAN, true));
			}
		});
	}
}
