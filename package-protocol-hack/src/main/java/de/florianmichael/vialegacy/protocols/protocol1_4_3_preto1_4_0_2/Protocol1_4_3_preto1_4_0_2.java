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

package de.florianmichael.vialegacy.protocols.protocol1_4_3_preto1_4_0_2;

import com.viaversion.viaversion.api.connection.UserConnection;
import de.florianmichael.vialegacy.api.EnZaProtocol;
import de.florianmichael.vialegacy.protocol.SplitterTracker;
import de.florianmichael.vialegacy.protocols.protocol1_4_4_5to1_4_3_pre.ClientboundPackets1_4_3_pre;
import de.florianmichael.vialegacy.protocols.protocol1_4_4_5to1_4_3_pre.ServerboundPackets1_4_3_pre;
import de.florianmichael.vialegacy.protocols.protocol1_7_0_5to1_6_4.ClientboundLoginPackets1_6_4;

public class Protocol1_4_3_preto1_4_0_2 extends EnZaProtocol<ClientboundPackets1_4_0_2, ClientboundPackets1_4_3_pre, ServerboundPackets1_4_0_2, ServerboundPackets1_4_3_pre> {

	public Protocol1_4_3_preto1_4_0_2() {
		super(ClientboundPackets1_4_0_2.class, ClientboundPackets1_4_3_pre.class, ServerboundPackets1_4_0_2.class, ServerboundPackets1_4_3_pre.class);
	}

	@Override
	public void init(UserConnection connection) {
		super.init(connection);

		connection.put(new SplitterTracker(connection, ClientboundPackets1_4_0_2.values(), ClientboundLoginPackets1_6_4.values()));
	}
}
