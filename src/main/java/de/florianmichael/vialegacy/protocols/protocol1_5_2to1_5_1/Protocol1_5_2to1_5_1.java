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

package de.florianmichael.vialegacy.protocols.protocol1_5_2to1_5_1;

import de.florianmichael.vialegacy.api.via.EnZaProtocol;
import de.florianmichael.vialegacy.protocols.protocol1_6_1to1_5_2.ClientboundPackets1_5_2;
import de.florianmichael.vialegacy.protocols.protocol1_6_1to1_5_2.ServerboundPackets1_5_2;

public class Protocol1_5_2to1_5_1 extends EnZaProtocol<ClientboundPackets1_5_1, ClientboundPackets1_5_2, ServerboundPackets1_5_1, ServerboundPackets1_5_2> {

	public Protocol1_5_2to1_5_1() {
		super(ClientboundPackets1_5_1.class, ClientboundPackets1_5_2.class, ServerboundPackets1_5_1.class, ServerboundPackets1_5_2.class);
	}
}
