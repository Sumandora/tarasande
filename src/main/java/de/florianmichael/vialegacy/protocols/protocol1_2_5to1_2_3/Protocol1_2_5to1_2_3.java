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

package de.florianmichael.vialegacy.protocols.protocol1_2_5to1_2_3;

import de.florianmichael.vialegacy.api.via.EnZaProtocol;
import de.florianmichael.vialegacy.protocols.protocol1_3_2to1_2_5.ClientboundPackets1_2_5;
import de.florianmichael.vialegacy.protocols.protocol1_3_2to1_2_5.ServerboundPackets1_2_5;

public class Protocol1_2_5to1_2_3 extends EnZaProtocol<ClientboundPackets1_2_3, ClientboundPackets1_2_5, ServerboundPackets1_2_3, ServerboundPackets1_2_5> {

	public Protocol1_2_5to1_2_3() {
		super(ClientboundPackets1_2_3.class, ClientboundPackets1_2_5.class, ServerboundPackets1_2_3.class, ServerboundPackets1_2_5.class);
	}
}
