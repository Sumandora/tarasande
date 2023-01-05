package de.florianmichael.viabeta.protocol.beta.protocolb1_6_0_6tob1_5_0_2.storage;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;

public class WorldTimeStorage extends StoredObject {

    public long time;

    public WorldTimeStorage(UserConnection user) {
        super(user);
    }

}
