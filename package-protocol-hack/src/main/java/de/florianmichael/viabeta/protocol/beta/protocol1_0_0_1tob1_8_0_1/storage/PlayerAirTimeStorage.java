package de.florianmichael.viabeta.protocol.beta.protocol1_0_0_1tob1_8_0_1.storage;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;

public class PlayerAirTimeStorage extends StoredObject {

    public final int MAX_AIR = 300;
    public int air = MAX_AIR;
    public boolean sentPacket = true;

    public PlayerAirTimeStorage(UserConnection user) {
        super(user);
    }

}
