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

package de.florianmichael.vialegacy.protocols.protocol1_4_3_preto1_4_2;

import de.florianmichael.vialegacy.api.EnZaProtocol;
import de.florianmichael.vialegacy.protocols.protocol1_4_5to1_4_3_pre.ClientboundPackets1_4_3_pre;
import de.florianmichael.vialegacy.protocols.protocol1_4_5to1_4_3_pre.ServerboundPackets1_4_3_pre;

public class Protocol1_4_3_preto1_4_2 extends EnZaProtocol<ClientboundPackets1_4_2, ClientboundPackets1_4_3_pre, ServerboundPackets1_4_2, ServerboundPackets1_4_3_pre> {

	public Protocol1_4_3_preto1_4_2() {
		super(ClientboundPackets1_4_2.class, ClientboundPackets1_4_3_pre.class, ServerboundPackets1_4_2.class, ServerboundPackets1_4_3_pre.class);
	}
}
