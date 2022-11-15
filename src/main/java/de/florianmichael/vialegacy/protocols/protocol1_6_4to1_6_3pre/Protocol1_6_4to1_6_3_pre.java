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

package de.florianmichael.vialegacy.protocols.protocol1_6_4to1_6_3pre;

import com.viaversion.viaversion.api.connection.UserConnection;
import de.florianmichael.vialegacy.api.EnZaProtocol;
import de.florianmichael.vialegacy.protocol.SplitterTracker;
import de.florianmichael.vialegacy.protocols.protocol1_7_5to1_6_4.ClientboundLoginPackets1_6_4;
import de.florianmichael.vialegacy.protocols.protocol1_7_5to1_6_4.ClientboundPackets1_6_4;
import de.florianmichael.vialegacy.protocols.protocol1_7_5to1_6_4.ServerboundPackets1_6_4;

public class Protocol1_6_4to1_6_3_pre extends EnZaProtocol<ClientboundPackets1_6_3_pre, ClientboundPackets1_6_4, ServerboundPackets1_6_3_pre, ServerboundPackets1_6_4> {

    public Protocol1_6_4to1_6_3_pre() {
        super(ClientboundPackets1_6_3_pre.class, ClientboundPackets1_6_4.class, ServerboundPackets1_6_3_pre.class, ServerboundPackets1_6_4.class);
    }

    @Override
    public void init(UserConnection connection) {
        super.init(connection);

        connection.put(new SplitterTracker(connection, ClientboundPackets1_6_3_pre.values(), ClientboundLoginPackets1_6_4.values()));
    }
}
