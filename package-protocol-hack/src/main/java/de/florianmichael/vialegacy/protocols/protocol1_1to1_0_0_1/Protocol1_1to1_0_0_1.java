package de.florianmichael.vialegacy.protocols.protocol1_1to1_0_0_1;

import com.viaversion.viaversion.api.connection.UserConnection;
import de.florianmichael.vialegacy.api.viaversion.EnZaProtocol;
import de.florianmichael.vialegacy.protocol.SplitterTracker;
import de.florianmichael.vialegacy.protocols.protocol1_2_1_3to1_1.ClientboundPackets1_1;
import de.florianmichael.vialegacy.protocols.protocol1_2_1_3to1_1.ServerboundPackets1_1;
import de.florianmichael.vialegacy.protocols.protocol1_6_4.ClientboundLoginPackets1_6_4;

public class Protocol1_1to1_0_0_1 extends EnZaProtocol<ClientboundPackets1_0_0_1, ClientboundPackets1_1, ServerboundPackets1_0_0_1, ServerboundPackets1_1> {

    public Protocol1_1to1_0_0_1() {
        super(ClientboundPackets1_0_0_1.class, ClientboundPackets1_1.class, ServerboundPackets1_0_0_1.class, ServerboundPackets1_1.class);
    }

    @Override
    public void init(UserConnection connection) {
        super.init(connection);

        connection.put(new SplitterTracker(connection, ClientboundPackets1_0_0_1.values(), ClientboundLoginPackets1_6_4.values()));
    }
}
